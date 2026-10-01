from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional
from app.schemas.event import Analysis

class ResponseActionBase(BaseModel):
    action_type: str
    reason: str
    status: str = "Completed"

class ResponseAction(ResponseActionBase):
    id: int
    incident_id: Optional[int]
    executed_by: int
    timestamp: datetime

    class Config:
        from_attributes = True

class IncidentBase(BaseModel):
    severity: str
    status: str = "Open"
    assigned_to: Optional[int] = None

class Incident(IncidentBase):
    id: int
    analysis_id: int
    created_at: datetime
    updated_at: datetime
    analysis: Optional[Analysis] = None
    responses: List[ResponseAction] = []

    class Config:
        from_attributes = True
