package rmi.account.client;

import java.rmi.*;
import java.rmi.server.*;

public class BankClient {
    
    public static void main(String[] args) {
        if (args.length == 2)
            (new BankClientFrame(args[0], args[1])).show();
        else
            (new BankClientFrame()).show();
    }
}
