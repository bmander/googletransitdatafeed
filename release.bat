
if "%1" == "" GOTO END

mkdir C:\SD\TransXChange2GoogleTransit_%1%2
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\License
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\dist
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\examplesInput
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\examplesOutput

xcopy Transxchange2GoogleTransit\tXCh2GT.bat C:\SD\TransXChange2GoogleTransit_%1%2
xcopy Transxchange2GoogleTransit\tXCh2GT.sh C:\SD\TransXChange2GoogleTransit_%1%2
xcopy Transxchange2GoogleTransit\License\*.txt C:\SD\TransXChange2GoogleTransit_%1%2\License /S
xcopy Transxchange2GoogleTransit\dist\*.jar C:\SD\TransXChange2GoogleTransit_%1%2\dist
xcopy Transxchange2GoogleTransit\examplesInput\*.xml C:\SD\TransXChange2GoogleTransit_%1%2\examplesInput /S 

if "%2" NEQ "dev" GOTO END
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\transxchange2GoogleTransitHandler
mkdir C:\SD\TransXChange2GoogleTransit_%1%2\ValidatorFiles

xcopy Transxchange2GoogleTransit\*.java C:\SD\TransXChange2GoogleTransit_%1%2 /S
xcopy Transxchange2GoogleTransit\*.class C:\SD\TransXChange2GoogleTransit_%1%2 /S
xcopy Transxchange2GoogleTransit\validate.sh C:\SD\TransXChange2GoogleTransit_%1%2
xcopy Transxchange2GoogleTransit\validate.bat C:\SD\TransXChange2GoogleTransit_%1%2
xcopy Transxchange2GoogleTransit\build.xml C:\SD\TransXChange2GoogleTransit_%1%2
xcopy Transxchange2GoogleTransit\ValidatorFiles\*.* C:\SD\TransXChange2GoogleTransit_%1%2\ValidatorFiles\*.* /E

:END
