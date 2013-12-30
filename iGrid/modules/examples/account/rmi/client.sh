#!/bin/sh

java -cp ../../classes -Djava.security.policy=policy rmi.account.client.BankClient yucca.cs.ttu.edu 1099
