package rmi.account.server;

import rmi.account.data.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

public class Impl2Launcher {
    static private int port = 1099;

    public static void main(String[] args) {
        Collection nameBalancePairs = getNameBalancePairs(args);
        Iterator i = nameBalancePairs.iterator();

        while (i.hasNext()) {
            NameBalancePair nextNameBalancePair = (NameBalancePair) i.next();

            launchServer(nextNameBalancePair);
        }
    }

    private static void launchServer(NameBalancePair serverDescription) {
        try {
            AccountImpl2 newAccount = new AccountImpl2(serverDescription.balance);
            RemoteStub stub = UnicastRemoteObject.exportObject(newAccount);

            Naming.rebind("rmi://localhost:" + port + '/' + serverDescription.name, stub);
            System.out.println("Account " + serverDescription.name + " successfully launched.");
        } catch (Exception e) {
        }
    }

    private static Collection getNameBalancePairs(String[] args) {
        int i;
        ArrayList returnValue = new ArrayList();

        for (i = 0; i < args.length; i += 2) {
            NameBalancePair nextNameBalancePair = new NameBalancePair();

            nextNameBalancePair.name = args[i];
            int cents = (new Integer(args[i + 1])).intValue();

            nextNameBalancePair.balance = new Money(cents);
            returnValue.add(nextNameBalancePair);
        }
        return returnValue;
    }

    private static class NameBalancePair {
        String name;
        Money balance;
    }
}
