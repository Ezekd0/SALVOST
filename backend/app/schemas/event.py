from pydantic import BaseModel
from datetime import datetime
from typing import Optional

class AnalysisBase(BaseModel):
    threat_score: float
    confidence: float
    classification: str
    attack_type: str
    explanation_json: str

class Analysis(AnalysisBase):
    id: int
    event_id: int
    timestamp: datetime

    class Config:
        from_attributes = True

class SecurityEventBase(BaseModel):
    source_ip: str
    destination_ip: str
    destination_port: int
    protocol: str
    flow_duration: float
    packet_rate: float
    bytes_rate: float
    is_demo: bool = False

class SecurityEventCreate(SecurityEventBase):
    pass

class SecurityEvent(SecurityEventBase):
    id: int
    timestamp: datetime
    analysis: Optional[Analysis] = None

    class Config:
        from_attributes = True
