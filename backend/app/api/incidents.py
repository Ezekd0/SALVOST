from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from app.database.connection import get_db
from app.models.incident import Incident, ResponseAction
from app.schemas.incident import Incident as IncidentSchema, ResponseAction as ResponseActionSchema
from app.api.auth import get_current_user
from app.models.user import User

router = APIRouter()

@router.get("/", response_model=List[IncidentSchema])
def list_incidents(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    return db.query(Incident).filter(Incident.user_id == current_user.id).order_by(Incident.created_at.desc()).all()

@router.get("/{incident_id}", response_model=IncidentSchema)
def get_incident(incident_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    incident = db.query(Incident).filter(Incident.id == incident_id, Incident.user_id == current_user.id).first()
    if not incident:
        raise HTTPException(status_code=404, detail="Incident not found")
    return incident

@router.get("/responses/recent", response_model=List[ResponseActionSchema])
def list_recent_responses(limit: int = 10, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    return db.query(ResponseAction).join(Incident).filter(Incident.user_id == current_user.id).order_by(ResponseAction.timestamp.desc()).limit(limit).all()
