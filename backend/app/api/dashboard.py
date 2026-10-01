from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.database.connection import get_db
from app.models.event import SecurityEvent, Analysis
from app.models.incident import Incident
from app.api.auth import get_current_user
from app.models.user import User

router = APIRouter()

@router.get("/summary")
def get_dashboard_summary(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    total_events = db.query(SecurityEvent).filter(SecurityEvent.user_id == current_user.id).count()
    detected_threats = db.query(Analysis).filter(Analysis.user_id == current_user.id, Analysis.classification != "Benign").count()
    critical_incidents = db.query(Incident).filter(Incident.user_id == current_user.id, Incident.severity == "Critical").count()
    contained_responses = db.query(Incident).filter(Incident.user_id == current_user.id, Incident.status == "Contained").count()
    
    return {
        "eventsAnalyzed": total_events,
        "threatsDetected": detected_threats,
        "criticalIncidents": critical_incidents,
        "contained": contained_responses
    }
