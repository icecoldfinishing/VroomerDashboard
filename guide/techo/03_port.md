(netstat -ano | findstr ":8089" | findstr "LISTENING") | ForEach-Object { $pid = ($_ -split '\s+')[-1]; taskkill /F /PID $pid }

netstat -ano | findstr :8089
taskkill /PID 9248 /F