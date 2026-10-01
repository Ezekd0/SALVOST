from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List
from app.database.connection import get_db
from app.models.event import SecurityEvent
from app.schemas.event import SecurityEvent as SecurityEventSchema
from app.api.auth import get_current_user
from app.models.user import User

router = APIRouter()

@router.get("/", response_model=List[SecurityEventSchema])
def list_events(limit: int = 100, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    return db.query(SecurityEvent).filter(SecurityEvent.user_id == current_user.id).order_by(SecurityEvent.timestamp.desc()).limit(limit).all()
