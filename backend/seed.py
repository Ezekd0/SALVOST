from app.database.connection import SessionLocal, engine, Base
from app.models.user import User
from app.security.auth import get_password_hash
import app.models # Register all models

# Create tables
Base.metadata.create_all(bind=engine)

db = SessionLocal()
user = db.query(User).filter(User.username == "demo").first()
if not user:
    demo_user = User(username="demo", email="demo@example.com", hashed_password=get_password_hash("demo123"), role="admin")
    db.add(demo_user)
    db.commit()
    print("Demo user seeded.")
else:
    print("Demo user already exists.")
db.close()
