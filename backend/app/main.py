from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api import auth, dashboard, analysis, incidents, events
from app.database.connection import engine, Base
import app.models # Register models

Base.metadata.create_all(bind=engine)

app = FastAPI(title="AI-CTDRS API")

# Setup CORS for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # For dev MVP
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
def health_check():
    return {"status": "ok", "service": "AI-CTDRS Backend"}

app.include_router(auth.router, prefix="/auth", tags=["Auth"])
app.include_router(dashboard.router, prefix="/dashboard", tags=["Dashboard"])
app.include_router(analysis.router, prefix="/analysis", tags=["Analysis"])
app.include_router(incidents.router, prefix="/incidents", tags=["Incidents"])
app.include_router(events.router, prefix="/events", tags=["Events"])
