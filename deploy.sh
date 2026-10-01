#!/bin/bash
cd /home/ezekdo/Downloads/Salvost/backend
source venv/bin/activate
uvicorn app.main:app --host 0.0.0.0 --port 8000 &
npx -y localtunnel --port 8000 > backend_url.txt &
sleep 5
BACKEND_URL=$(grep -o 'https://.*\.loca\.lt' backend_url.txt | head -1)
echo "Backend URL: $BACKEND_URL"
echo "VITE_API_URL=$BACKEND_URL" > ../frontend/.env
cd ../frontend
npm run build
npm run preview -- --host 0.0.0.0 --port 4173 &
npx -y localtunnel --port 4173 > frontend_url.txt &
sleep 5
FRONTEND_URL=$(grep -o 'https://.*\.loca\.lt' frontend_url.txt | head -1)
echo "Frontend URL: $FRONTEND_URL"
wait
