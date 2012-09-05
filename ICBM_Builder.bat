@echo off
set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER%>buildnumber.txt

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

set FILE_NAME=ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip

::ZIP-UP
cd reobf\minecraft\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%" "atomicscience\"

"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%" "icbm\"

cd ..\..\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%" "mcmod*.info"

cd includes\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%" "icbm\"

"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Backup\icbm\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "icbm\"
cd ..\

cd src\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Backup\icbm\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*\icbm\"

::COPY TO DROPBOX
xcopy "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%" "C:\Users\Henry\Dropbox\ICBM" /E

echo %FILE_NAME%>>"C:\Users\Henry\Dropbox\ICBM\info.txt"
echo Done building %FILE_NAME% for UE %UE_VERSION%

pause