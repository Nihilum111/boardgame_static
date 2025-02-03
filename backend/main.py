#!/usr/bin/env python3
# Импорты
from fastapi import FastAPI, Depends, HTTPException, status, Query
from pydantic import BaseModel, Field, field_validator, ConfigDict
from sqlalchemy import create_engine, Column, Integer, String, Text, Date
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from typing import List, Optional
import uvicorn
import logging
from datetime import datetime, date
from dotenv import load_dotenv
import os

# Загрузка переменных окружения
load_dotenv()

# Логирование
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Подключение к базе данных
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://user:password@localhost:5432/dbname")
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# SQLAlchemy модели
class Game(Base):
    __tablename__ = "games"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    players = Column(String, nullable=True)
    scores = Column(String, nullable=True)
    date = Column(Date, nullable=True)  # Тип DATE в SQLAlchemy
    places = Column(String, nullable=True)

class BoardGame(Base):
    __tablename__ = "boardgames"
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False)
    title_ru = Column(String(255), nullable=True)
    short_description = Column(Text, nullable=True)
    logo_url = Column(String, nullable=True)

class BoardGameDetailResponse(BaseModel):
    id: int
    title: str
    short_description: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)

# Создание таблиц (если их еще нет)
Base.metadata.create_all(bind=engine)

###############################################################################
# Кастомный тип CustomDate для поля date с детальным логированием
###############################################################################
class CustomDate:
    @classmethod
    def __get_validators__(cls):
        yield cls.validate

    @classmethod
    def validate(cls, value, info) -> Optional[date]:
        logger.info(f"[CustomDate.validate] Начало валидации значения для date: {value} (тип: {type(value)})")
        if value is None or value == "":
            logger.info("[CustomDate.validate] Значение для date отсутствует или пустое, возвращаем None")
            return None
        if isinstance(value, date):
            logger.info("[CustomDate.validate] Значение уже является объектом datetime.date, возвращаем его без изменений")
            return value
        if isinstance(value, str):
            logger.info(f"[CustomDate.validate] Обнаружена строка для date: '{value}'")
            # Перебор форматов для преобразования
            for fmt in ("%Y-%m-%d", "%d/%m/%Y"):
                logger.info(f"[CustomDate.validate] Попытка преобразования даты '{value}' с использованием формата '{fmt}'")
                try:
                    parsed_date = datetime.strptime(value, fmt).date()
                    logger.info(f"[CustomDate.validate] Дата успешно преобразована с форматом '{fmt}': {parsed_date}")
                    return parsed_date
                except ValueError as e:
                    logger.warning(f"[CustomDate.validate] Не удалось преобразовать дату '{value}' с форматом '{fmt}': {e}")
                    continue
            logger.error(f"[CustomDate.validate] Все попытки преобразования даты '{value}' завершились неудачей")
            raise ValueError("Неверный формат даты. Ожидается 'YYYY-MM-DD' или 'DD/MM/YYYY'.")
        logger.error(f"[CustomDate.validate] Неверный тип для поля date: {type(value)}. Ожидается строка или datetime.date.")
        raise TypeError("Неверный тип для поля date. Ожидается строка или datetime.date.")

###############################################################################
# Pydantic модели
###############################################################################
class GameCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=255)
    players: Optional[str] = None
    scores: Optional[str] = None
    # Используем наш кастомный тип для поля date, чтобы после валидации всегда был объект datetime.date
    date: Optional[CustomDate] = None
    places: Optional[str] = None

# Модель ответа. Обратите внимание, что здесь мы не присваиваем "= None" для date,
# а просто указываем, что тип поля может быть date или None.
class GameResponse(BaseModel):
    id: int
    name: str
    players: Optional[str]
    scores: Optional[str]
    date: date | None
    places: Optional[str]

    model_config = ConfigDict(
        from_attributes=True,
        json_encoders={date: lambda v: v.isoformat()}  # Сериализация date в ISO-формат
    )

class BoardGameResponse(BaseModel):
    id: int
    title: str

    model_config = ConfigDict(from_attributes=True)
###############################################################################
# FastAPI приложение и зависимости
###############################################################################
app = FastAPI(
    title="API для BoardGameStatistic",
    description="API для управления статистикой настольных игр",
    version="1.0.0"
)

# Зависимость для получения сессии БД
def get_db():
    db = SessionLocal()
    try:
        yield db
    except Exception as e:
        db.rollback()
        logger.error(f"Ошибка работы с базой данных: {str(e)}")
        raise e
    finally:
        db.close()

###############################################################################
# Обработчики API
###############################################################################
@app.get("/")
def read_root():
    return {"message": "FastAPI API для BoardGameStatistic работает"}

@app.get("/games/", response_model=List[GameResponse])
def get_games(skip: int = 0, limit: int = Query(10, ge=1, le=100), db: Session = Depends(get_db)):
    games = db.query(Game).offset(skip).limit(limit).all()
    logger.info(f"Получено {len(games)} записей о сыгранных играх")
    return games

@app.post("/games/", response_model=GameResponse, status_code=status.HTTP_201_CREATED)
def create_game(game: GameCreate, db: Session = Depends(get_db)):
    try:
        logger.info(f"Получены данные для создания игры: {game.dict()}")
        logger.info(f"Тип поля date после валидации: {type(game.date)}; значение: {game.date}")
        # Создание новой записи (game.date уже является объектом datetime.date или None)
        new_game = Game(
            name=game.name,
            players=game.players,
            scores=game.scores,
            date=game.date,
            places=game.places
        )
        logger.info(f"Создаётся объект SQLAlchemy с date: {new_game.date}")
        db.add(new_game)
        db.commit()
        db.refresh(new_game)
        logger.info(f"Создана новая запись игры с ID {new_game.id}")
        return new_game
    except Exception as e:
        logger.error(f"Ошибка создания записи игры: {str(e)}")
        db.rollback()
        raise HTTPException(status_code=500, detail="Внутренняя ошибка сервера")

@app.get("/games/{id}", response_model=GameResponse)
def get_game_by_id(id: int, db: Session = Depends(get_db)):
    game = db.query(Game).filter(Game.id == id).first()
    if not game:
        raise HTTPException(status_code=404, detail="Игра не найдена")
    return game

@app.get("/boardgames/", response_model=List[BoardGameResponse])
def get_boardgames(
    search: Optional[str] = None,
    skip: int = 0,
    limit: int = Query(10, ge=1, le=100),
    db: Session = Depends(get_db)
):
    query = db.query(BoardGame)
    if search:
        query = query.filter(BoardGame.title.ilike(f"%{search}%"))
    boardgames = query.offset(skip).limit(limit).all()
    logger.info(f"Получено {len(boardgames)} настольных игр (search: {search})")
    return boardgames
@app.get("/boardgames/{id}", response_model=BoardGameDetailResponse)
def get_boardgame_by_id(id: int, db: Session = Depends(get_db)):
    boardgame = db.query(BoardGame).filter(BoardGame.id == id).first()
    if boardgame is None:
        raise HTTPException(status_code=404, detail="Игра не найдена")
    return boardgame
# Запуск приложения
if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8008, reload=True)
