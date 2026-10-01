from sqlalchemy.orm import Session
from app.models.incident import Incident, ResponseAction

def execute_response(severity: str, analysis_id: int, user_id: int, db: Session):
    # Determine Action
    if severity == "Low":
        action = "Allow"
        reason = "Traffic classified as benign."
        status = "Completed"
    elif severity == "Moderate":
        action = "Monitor"
        reason = "Suspicious traffic detected. Monitoring initiated."
        status = "Open"
    elif severity == "High":
        action = "Contain"
        reason = "Malicious behaviour detected. Demo event isolated."
        status = "Contained"
    elif severity in ["Critical"]:
        action = "Isolate Demo Event"
        reason = "Critical threat detected. Isolation protocols activated."
        status = "Contained"
    else:
        action = "Log"
        reason = "Unknown severity."
        status = "Completed"

    # Create Incident if it's High or Critical, or just open a minor incident for Moderate
    incident = None
    if severity != "Low":
        incident = Incident(
            analysis_id=analysis_id,
            severity=severity,
            status=status,
            user_id=user_id,
            assigned_to=None
        )
        db.add(incident)
        db.commit()
        db.refresh(incident)

    # Log Response Action
    response = ResponseAction(
        incident_id=incident.id if incident else None,
        action_type=action,
        reason=reason,
        status="Completed", # The action itself is completed
        executed_by=user_id
    )
    db.add(response)
    db.commit()
    db.refresh(response)
    
    return response, incident
