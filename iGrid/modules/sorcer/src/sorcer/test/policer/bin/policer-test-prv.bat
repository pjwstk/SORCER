@echo on

rem This script starts a SORCER example arithmetic service provider

set PORTAL_URL=http://%IGRID_PORTAL_HOST%:%IGRID_PORTAL_PORT%
set CODEBASE_URL=http://%IGRID_WEBSTER%:%IGRID_WEBSTER_PORT%
set MODULE=%IGRID_HOME%\modules\sorcer\src\sorcer\test\policer

set classpath=-cp %IGRID_HOME%\lib\arithemtic.jar;%IGRID_HOME%\lib\jgapp.jar;%IGRID_HOME%\lib\sorcer.jar;%IGRID_HOME%\common\jini-ext.jar;

"%JAVA_HOME%"\bin\java -server %classpath% -Djava.security.manager -Djava.util.logging.config.file=%IGRID_HOME%\configs\sorcer.logging -Djava.security.policy=%IGRID_HOME%\policy\policy.all -Djava.protocol.handler.pkgs=net.jini.url -Dprovider.codebase=%CODEBASE_URL%/arithmetic-dl.jar;%CODEBASE_URL%/sdm-dl.jar; -Dprovider.classpath=%IGRID_HOME%\lib\policer-test.jar; -Dprovider.impl=sorcer.test.policer.src.ArithmeticProviderForPolicerImpl -jar %IGRID_HOME%\common\start.jar %MODULE%\configs\policer-test-prv.config