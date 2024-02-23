@echo OFF

reg Query "HKLM\Hardware\Description\System\CentralProcessor\0" | find /i "x86" > NUL && set arch=32 || set arch=64

if %arch%==32 GOTO :UNSUPPORTED
if %arch%==64 GOTO :SUPPORTED

:UNSUPPORTED
echo "Your architecture is unsupported. You may have to use the CLI interface only."
EXIT 1

:SUPPORTED
set /p port="Enter the port you want to use: "
set /p server="Enter the server hostname/IP address: "
set /p rmihostname="Enter your RMI Hostname, if needed (otherwise, you can leave it clear): "

java -jar ClientSelector-AM34-x64.jar %port% %server% %rmihostname%

@pause