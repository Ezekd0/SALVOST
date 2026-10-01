from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.sql import func
from app.database.connection import Base
from sqlalchemy.orm import relationship

class Incident(Base):
    __tablename__ = "incidents"

    id = Column(Integer, primary_key=True, index=True)
    analysis_id = Column(Integer, ForeignKey("analyses.id"))
    severity = Column(String) # Low, Moderate, High, Critical
    status = Column(String, default="Open") # Open, Investigating, Contained, Closed
    assigned_to = Column(Integer, ForeignKey("users.id"), nullable=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    user = relationship("User", foreign_keys=[user_id], back_populates="incidents")
    analysis = relationship("Analysis", back_populates="incident")
    responses = relationship("ResponseAction", back_populates="incident")

class ResponseAction(Base):
    __tablename__ = "response_actions"

    id = Column(Integer, primary_key=True, index=True)
    incident_id = Column(Integer, ForeignKey("incidents.id"))
    action_type = Column(String) # Monitor, Contain, Isolate
    reason = Column(String)
    status = Column(String, default="Completed")
    executed_by = Column(Integer, ForeignKey("users.id"))
    timestamp = Column(DateTime(timezone=True), server_default=func.now())

    incident = relationship("Incident", back_populates="responses")
