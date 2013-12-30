package rmi.account.server;

import rmi.account.data.*;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.*;

public class ImplLauncher {
    private static int port = 1099;
    
    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(new RMISecurityManager());

	Collection nameBalancePairs = getNameBalancePairs(args);
        Iterator i = nameBalancePairs.iterator();
	
        while (i.hasNext()) {
            NameBalancePair nextNameBalancePair = (NameBalancePair) i.next();

            launchServer(nextNameBalancePair);
        }
    }

    private static void launchServer(NameBalancePair serverDescription) {
        try {
            AccountImpl newAccount = new AccountImpl(serverDescription.balance);
            Naming.rebind("rmi://localhost:" + port + '/' + serverDescription.name, newAccount);
            System.out.println("Account " + serverDescription.name + " successfully launched.");
        } catch (Exception e) {
	    e.printStackTrace();
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
