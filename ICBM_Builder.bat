@echo off
set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
del buildnumber.txt
echo %BUILD_NUMBER% >> buildnumber.txt

runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

cd reobf\minecraft\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip" "atomicscience\"

"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip" "icbm\"

cd ..\..\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip" "mcmod*.info"

cd includes\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip" "icbm\"

"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Backup\icbm\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "icbm\"
cd ..\

cd src\
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Backup\icbm\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*\icbm\"

pause