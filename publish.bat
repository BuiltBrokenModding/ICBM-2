::ICBM BUILDER
@echo off
echo Promotion Type?
set /p PROMOTION=

set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER% >buildnumber.txt

set FILE_NAME1=ICBM_Explosion_v%MODVERSION%.%BUILD_NUMBER%.jar
set FILE_NAME2=ICBM_Contraption_v%MODVERSION%.%BUILD_NUMBER%.jar
set FILE_NAME3=ICBM_Sentry_v%MODVERSION%.%BUILD_NUMBER%.jar
set API_NAME=ICBM_v%MODVERSION%.%BUILD_NUMBER%_api.zip

if %PROMOTION%==* (
	echo %MODVERSION% >recommendedversion.txt
)

echo Starting to build ICBM

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
:: BUILD ICBM EXPLOSION
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "atomicscience\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "mffs\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "dan200\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "ic2\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "railcraft\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "universalelectricity\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "icbm\api\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "icbm\core\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "org\"

"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME1%" "icbm\zhapin\"

:: BUILD ICBM CONTRAPTION
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "atomicscience\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "mffs\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "dan200\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "ic2\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "railcraft\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "universalelectricity\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "icbm\api\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "icbm\core\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "org\"

"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME2%" "icbm\wanyi\"

:: BUILD ICBM SENTRY
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "atomicscience\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "mffs\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "dan200\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "ic2\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "railcraft\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "universalelectricity\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "icbm\api\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "icbm\core\"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "org\"

"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME3%" "icbm\gangshao\"

cd ..\..\

cd resources\
"..\..\7za.exe" a "..\builds\%FILE_NAME1%" "*"
"..\..\7za.exe" a "..\builds\%FILE_NAME2%" "*"
"..\..\7za.exe" a "..\builds\%FILE_NAME3%" "*"
"..\..\7za.exe" a "..\builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "*" -phenry
cd ..\
cd src\
"..\..\7za.exe" a "..\builds\ICBM_v%MODVERSION%.%BUILD_NUMBER%_backup.zip" "minecraft\icbm\" -phenry
"..\..\7za.exe" a "..\builds\%API_NAME%" "minecraft\icbm\api\"
cd ..\

::UPDATE INFO FILE
echo %PROMOTION% %FILE_NAME1% %FILE_NAME2% %FILE_NAME3% %API_NAME%>>info.txt

::GENERATE FTP Script
echo open calclavia.com>ftpscript.txt
echo icbm@calclavia.com>>ftpscript.txt
echo 9ZxLl43ur1Gv>>ftpscript.txt
echo binary>>ftpscript.txt
echo put "recommendedversion.txt">>ftpscript.txt
echo put "builds\%FILE_NAME1%">>ftpscript.txt
echo put "builds\%FILE_NAME2%">>ftpscript.txt
echo put "builds\%FILE_NAME3%">>ftpscript.txt
echo put "builds\%API_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building ICBM

pause