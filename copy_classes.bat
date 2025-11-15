@echo off
echo ========================================
echo  Copiando classes para WEB-INF
echo ========================================

REM Criar pasta classes se não existir
if not exist "web\WEB-INF\classes" mkdir web\WEB-INF\classes

REM Limpar classes antigas
echo Limpando classes antigas...
del /S /Q web\WEB-INF\classes\* 2>nul

REM Copiar novas classes compiladas
echo Copiando novas classes...
xcopy /E /I /Y out\production\help-desk-ucb\* web\WEB-INF\classes\

echo.
echo ========================================
echo  Classes copiadas com sucesso!
echo ========================================
echo.
echo Estrutura criada em: web\WEB-INF\classes\
echo.
pause