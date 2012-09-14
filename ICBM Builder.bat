@echo off
echo What version is Universal Electricity for this build? Parameters?
set /p UE_VERSION=

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
"..\..\7za.exe" a "..\..\Builds\%FILE_NAME%" "atomicscience\"
"..\..\7za.exe" a "..\..\Builds\%FILE_NAME%" "icbm\"

cd ..\..\
"7za.exe" a "Builds\%FILE_NAME%" "mcmod.info"

cd resources\
"..\7za.exe" a "..\Builds\%FILE_NAME%" "icbm\"
"..\7za.exe" a "..\Backup\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "icbm\" -phenry
cd ..\
cd src\
"..\7za.exe" a "..\Backup\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*\icbm\" -phenry
cd ..\

::UPDATE INFO FILE
echo %FILE_NAME% %UE_VERSION%>>info.txt

::GENERATE FTP Script
echo open www.calclavia.com>ftpscript.txt
echo icbm@calclavia.com>>ftpscript.txt
echo ICBMmod>>ftpscript.txt
echo binary>>ftpscript.txt
echo put "Builds\%FILE_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause