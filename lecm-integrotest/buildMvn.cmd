date /t
time /t

rem arg is set - skip cleanup
if NOT %1x==x goto compile
call mvn clean

:compile
call mvn install

date /t
time /t


pause