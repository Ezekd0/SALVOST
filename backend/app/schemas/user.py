from pydantic import BaseModel, EmailStr
from datetime import datetime
from typing import Optional

class UserBase(BaseModel):
    full_name: str
    username: str
    email: EmailStr
    role: str = "analyst"

class UserCreate(UserBase):
    password: str

class UserRegister(UserBase):
    password: str
    confirm_password: str

class User(UserBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    username: Optional[str] = None
