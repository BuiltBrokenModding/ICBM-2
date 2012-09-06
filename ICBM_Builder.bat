@echo off
set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER% >buildnumber.txt

set FILE_NAME=ICBM_v%MODVERSION%.%BUILD_NUMBER%.zip

echo Starting to build %FILE_NAME%

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

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
"C:\Users\Henry\Documents\GitHub\ICBM\7za.exe" a "E:\Document\Computer Science\Minecraft Modding\Backup\icbm\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*\icbm\" -pHENRY
cd ..\

::UPDATE INFO FILE
echo What version is Universal Electricity for this build? Parameters?
set /p UE_VERSION=
echo %FILE_NAME% %UE_VERSION%>>info.txt

::GENERATE FTP Script
echo open www.calclavia.com>ftpscript.txt
echo icbm@calclavia.com>>ftpscript.txt
echo ICBMmod>>ftpscript.txt
echo put "E:\Document\Computer Science\Minecraft Modding\Releases\ICBM Builds\%FILE_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause