from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Boolean
from sqlalchemy.sql import func
from app.database.connection import Base
from sqlalchemy.orm import relationship

class SecurityEvent(Base):
    __tablename__ = "security_events"

    id = Column(Integer, primary_key=True, index=True)
    source_ip = Column(String, index=True)
    destination_ip = Column(String, index=True)
    destination_port = Column(Integer)
    protocol = Column(String)
    flow_duration = Column(Float)
    packet_rate = Column(Float)
    bytes_rate = Column(Float)
    timestamp = Column(DateTime(timezone=True), server_default=func.now())
    is_demo = Column(Boolean, default=False)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True)
    
    user = relationship("User", back_populates="events")
    analysis = relationship("Analysis", back_populates="event", uselist=False)

class Analysis(Base):
    __tablename__ = "analyses"

    id = Column(Integer, primary_key=True, index=True)
    event_id = Column(Integer, ForeignKey("security_events.id"))
    threat_score = Column(Float)
    confidence = Column(Float)
    classification = Column(String) # Benign, Suspicious, Malicious, Critical
    attack_type = Column(String) # Port Scan, DDoS, etc.
    explanation_json = Column(String) # JSON of feature contributions
    timestamp = Column(DateTime(timezone=True), server_default=func.now())
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True)
    
    user = relationship("User", back_populates="analyses")
    event = relationship("SecurityEvent", back_populates="analysis")
    incident = relationship("Incident", back_populates="analysis", uselist=False)
