@echo off
echo ========================================
echo  DEPLOY COMPLETO - Help Desk UCB
echo ========================================
echo.

REM ==================================
REM 1. CRIAR ESTRUTURA
REM ==================================
echo [1/5] Criando estrutura de pastas...
if not exist "web\WEB-INF\classes" mkdir web\WEB-INF\classes
if not exist "web\WEB-INF\lib" mkdir web\WEB-INF\lib

REM ==================================
REM 2. LIMPAR ARQUIVOS ANTIGOS
REM ==================================
echo [2/5] Limpando arquivos antigos...
del /S /Q web\WEB-INF\classes\*.class 2>nul
del /Q web\WEB-INF\classes\*.properties 2>nul

REM ==================================
REM 3. COPIAR CLASSES COMPILADAS
REM ==================================
echo [3/5] Copiando classes compiladas...
xcopy /E /I /Y out\production\helpdesk2.0\* web\WEB-INF\classes\

if errorlevel 1 (
    echo ERRO: Falha ao copiar classes!
    echo Certifique-se de fazer Build do projeto primeiro.
    pause
    exit /b 1
)

REM ==================================
REM 4. COPIAR config.properties
REM ==================================
echo [4/5] Copiando config.properties...

if exist "config.properties" (
    copy /Y config.properties web\WEB-INF\classes\
    echo    OK: config.properties copiado
) else (
    echo    AVISO: config.properties nao encontrado na raiz!
    echo    Criando config.properties padrao...

    (
        echo # Configuracoes do MySQL
        echo db.mysql.url=jdbc:mysql://localhost:3306/helpdesk
        echo db.mysql.user=helpdesk_app
        echo db.mysql.password=helpdesk2025
        echo db.mysql.driver=com.mysql.cj.jdbc.Driver
        echo.
        echo # Configuracoes do MongoDB
        echo db.mongo.host=localhost
        echo db.mongo.port=27017
        echo db.mongo.database=helpdesk_logs
        echo.
        echo # Seguranca
        echo security.bcrypt.rounds=12
    ) > web\WEB-INF\classes\config.properties

    echo    OK: config.properties criado automaticamente
)

REM ==================================
REM 5. COPIAR BIBLIOTECAS JAR
REM ==================================
echo [5/5] Copiando bibliotecas JAR...

if not exist "lib\*.jar" (
    echo    ERRO: Pasta lib\ vazia ou nao existe!
    echo    Baixe as bibliotecas primeiro.
    pause
    exit /b 1
)

copy /Y lib\*.jar web\WEB-INF\lib\

if errorlevel 1 (
    echo    ERRO: Falha ao copiar bibliotecas!
    pause
    exit /b 1
)

echo    OK: Bibliotecas copiadas

REM ==================================
REM VERIFICACAO FINAL
REM ==================================
echo.
echo ========================================
echo  VERIFICACAO FINAL
echo ========================================
echo.

echo Verificando WEB-INF/classes/:
if exist "web\WEB-INF\classes\Controller" (
    echo    [OK] Controller/
) else (
    echo    [ERRO] Controller/ NAO ENCONTRADO!
)

if exist "web\WEB-INF\classes\Servlet" (
    echo    [OK] Servlet/
) else (
    echo    [ERRO] Servlet/ NAO ENCONTRADO!
)

if exist "web\WEB-INF\classes\config.properties" (
    echo    [OK] config.properties
) else (
    echo    [ERRO] config.properties NAO ENCONTRADO!
)

echo.
echo Verificando WEB-INF/lib/:
dir /B web\WEB-INF\lib\*.jar

echo.
echo ========================================
echo  DEPLOY CONCLUIDO!
echo ========================================
echo.
echo Proximos passos:
echo 1. Restart Tomcat (Parar + Iniciar)
echo 2. Acessar: http://localhost:8080/helpdesk
echo 3. Login: admin@helpdesk.com / Admin123!
echo.
pause