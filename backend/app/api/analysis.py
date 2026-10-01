from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database.connection import get_db
from app.models.event import SecurityEvent, Analysis
from app.schemas.event import SecurityEventCreate, SecurityEvent as SecurityEventSchema, Analysis as AnalysisSchema
from app.api.auth import get_current_user
from app.models.user import User
from app.ml.predict import predict_event
from app.response.engine import execute_response
import traceback

router = APIRouter()

@router.post("/run", response_model=SecurityEventSchema)
def run_analysis(event_in: SecurityEventCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    try:
        # Create event
        db_event = SecurityEvent(**event_in.model_dump(), user_id=current_user.id)
        db.add(db_event)
        db.commit()
        db.refresh(db_event)
        
        # Predict
        event_dict = event_in.model_dump()
        prediction_result = predict_event(event_dict)
        
        import json
        
        # Create Analysis
        db_analysis = Analysis(
            event_id=db_event.id,
            threat_score=prediction_result["threat_score"],
            confidence=prediction_result["confidence"],
            classification=prediction_result["classification"],
            attack_type=prediction_result["classification"] if prediction_result["classification"] != "Benign" else "None",
            explanation_json=json.dumps(prediction_result["explanation"]),
            user_id=current_user.id
        )
        db.add(db_analysis)
        db.commit()
        db.refresh(db_analysis)
        
        # Execute Response
        execute_response(prediction_result["severity"], db_analysis.id, current_user.id, db)
        
        # Refresh event to include analysis
        db.refresh(db_event)
        return db_event
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/{analysis_id}", response_model=AnalysisSchema)
def get_analysis(analysis_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    analysis = db.query(Analysis).filter(Analysis.id == analysis_id).first()
    if not analysis:
        raise HTTPException(status_code=404, detail="Analysis not found")
    return analysis
