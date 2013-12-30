/*
 The only difference between this and AccountImpl is that
 AccountImpl extends UnicastRemote.
 */
package rmi.account.server;

import rmi.account.*;
import rmi.account.data.*;
import java.rmi.*;
import java.rmi.server.*;


public class AccountImpl2 implements Account {
    private Money balance;

    public AccountImpl2(Money startingBalance)
        throws RemoteException {
        balance = startingBalance;
    }

    public Money getBalance()
        throws RemoteException {
        return balance;
    }

    public void makeDeposit(Money amount)
        throws RemoteException, NegativeAmountException {
        checkForNegativeAmount(amount);
        balance.add(amount);
        return;
    }

    public void makeWithdrawal(Money amount)
        throws RemoteException, OverdraftException, NegativeAmountException {
        checkForNegativeAmount(amount);
        checkForOverdraft(amount);
        balance.subtract(amount);
        return;
    }

    public boolean equals(Object object) {
        // three cases. Either it's us, or it's our stub, or it's
        // not equal.

        if (object instanceof AccountImpl2) {
            return (object == this);
        }
        if (object instanceof RemoteStub) {
            try {
                RemoteStub ourStub = (RemoteStub) RemoteObject.toStub(this);

                return ourStub.equals(object);
            } catch (NoSuchObjectException e) {
                // we're not listening on a port, therefore it's not our
                // stub
            }
        }
        return false;
    }

    public int hashCode() {
        try {
            Remote ourStub = RemoteObject.toStub(this);

            return ourStub.hashCode();
        } catch (NoSuchObjectException e) {
        }
        return super.hashCode();
    }
    
    private void checkForNegativeAmount(Money amount)
        throws NegativeAmountException {
        int cents = amount.getCents();

        if (0 > cents) {
            throw new NegativeAmountException();
        }
    }

    private void checkForOverdraft(Money amount)
        throws OverdraftException {
        if (amount.greaterThan(balance)) {
            throw new OverdraftException(false);
        }
        return;
    }
} 

