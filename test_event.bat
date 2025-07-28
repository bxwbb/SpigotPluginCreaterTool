@echo off
echo 启动中...
timeout /t 1 /nobreak >nul
echo 请输入服务器名称:
set /p serverName=
echo 服务器名称已设置为: %serverName%
echo 启动完成
pause
