JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
JINILIB=/Users/sobolemw/jini2_1/lib
POLICY=-Djava.security.policy=/Users/sobolemw/incax/policy.all
CP=/Users/sobolemw/incax/incax-ce-browser.jar:/Users/sobolemw/incax/lib/serviceui.jar:$JINILIB/jsk-platform.jar:$JINILIB/jini-ext.jar:$JINILIB/sun-util.jar
$JAVA_HOME/bin/java -Djava.protocol.handler.pkgs=net.jini.url -cp $CP $POLICY -Xdock:name="Inca X Service Browser" IncaXBrowserCE $1
