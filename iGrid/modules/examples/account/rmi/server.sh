#!/bin/sh

java -cp ../../lib/account.server.jar -Djava.security.policy=policy.all -Djava.rmi.server.codebase=http://yucca.cs.ttu.edu/cs5376/lib/account-client.jar rmi.account.server.ImplLauncher Mike 10000 Monika 1000
