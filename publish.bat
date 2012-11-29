::ICBM BUILDER
@echo off
echo Promotion Type?
set /p PROMOTION=

set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER% >buildnumber.txt

set FILE_NAME=ICBM_v%MODVERSION%.%BUILD_NUMBER%.jar
set API_NAME=ICBM_v%MODVERSION%.%BUILD_NUMBER%_api.zip

echo Starting to build %FILE_NAME%

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "atomicscience\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "chb\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "dan200\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "ic2\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "railcraft\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "buildcraft\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "universalelectricity\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "icbm\"

cd ..\..\

cd resources\
"..\..\7za.exe" a "..\builds\%FILE_NAME%" "*"
"..\..\7za.exe" a "..\builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*" -phenry
cd ..\
cd src\
"..\..\7za.exe" a "..\builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*\icbm\" -phenry
"..\..\7za.exe" a "..\builds\%API_NAME%" "*\icbm\api\"
cd ..\

::UPDATE INFO FILE
echo %PROMOTION% %FILE_NAME%>>info.txt

::GENERATE FTP Script
echo open www.calclavia.com>ftpscript.txt
echo icbm@calclavia.com>>ftpscript.txt
echo 9ZxLl43ur1Gv>>ftpscript.txt
echo binary>>ftpscript.txt
::Upload texture file
echo put "resources\icbm\textures\blocks.png">>ftpscript.txt
echo put "resources\icbm\textures\items.png">>ftpscript.txt
echo put "builds\%FILE_NAME%">>ftpscript.txt
echo put "builds\%API_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause