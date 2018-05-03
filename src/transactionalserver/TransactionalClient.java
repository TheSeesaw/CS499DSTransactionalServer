/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author Krasis
 */
public class TransactionalClient 
{
    static int numAccounts;
    static int numTransactions;
    static int serverPort;
    static String serverHost;
    static ArrayList<TransactionServerProxy> proxies;
    static Random rGen;
    
    public TransactionalClient(int aPort, String aHost, int accounts, int transactions)
    {
        TransactionalClient.serverPort = aPort;
        TransactionalClient.serverHost = aHost;
        TransactionalClient.numAccounts = accounts;
        TransactionalClient.numTransactions = transactions;
        TransactionalClient.proxies = new ArrayList<TransactionServerProxy>(TransactionalClient.numTransactions);
        TransactionalClient.rGen = new Random();
    }
    
    public void run()
    {
        int transactionIndex = 0;
        for (; transactionIndex < TransactionalClient.numTransactions; transactionIndex++)
        {
            TransactionServerProxyThread pThread = new TransactionServerProxyThread(transactionIndex);
            pThread.start();
        }
    }
    
    private class TransactionServerProxyThread extends Thread
    {
        int id;
        int fromAccount;
        int toAccount;
        int initialBalance;
        int endBalance;
        int transferAmount;
        TransactionServerProxy proxy;
        
        private TransactionServerProxyThread(int transactionID)
        {
            this.id = transactionID;
            this.fromAccount = TransactionalClient.rGen.nextInt(TransactionalClient.numAccounts);
            this.toAccount = TransactionalClient.rGen.nextInt(TransactionalClient.numAccounts);
            while (this.toAccount == this.fromAccount) {
                // generate a new toAccount number until they don't match
                this.toAccount = TransactionalClient.rGen.nextInt(TransactionalClient.numAccounts);
            }
            // create a proxy to work through and add it to known proxies
            this.proxy = new TransactionServerProxy(TransactionalClient.serverPort, TransactionalClient.serverHost);
            TransactionalClient.proxies.add(this.proxy);
        }
        
        public void run()
        {
            // open a connection to the server
            this.proxy.openConnection();
            // read account value from the server
            this.initialBalance = (Integer)this.proxy.read(this.fromAccount);
            // modify account by random value between 1 and 10
            this.transferAmount = TransactionalClient.rGen.nextInt(10) + 1;
            this.endBalance = initialBalance - transferAmount;
            // write account value back to the server
            this.proxy.write(fromAccount, endBalance);
            // withdrawal finished, start deposit to second account
            // read second account value
            this.initialBalance = (Integer)this.proxy.read(this.toAccount);
            // deposit transfer amount
            this.endBalance = this.initialBalance + this.transferAmount;
            this.proxy.write(toAccount, endBalance);
            // transaction complete, close the connection
            this.proxy.closeConnection();
        }
    }
    
    public static void main(String[] args) 
    {
        // get number of accounts and transactions from the command line
        // NOTE: hardcoded for now
        int serverPort = Integer.parseInt(args[0]);
        String serverHost = args[1];
        int accParam = Integer.parseInt(args[2]);
        int transParam = Integer.parseInt(args[3]);
        
        // create a new client
        TransactionalClient client = new TransactionalClient(serverPort, serverHost, accParam, transParam);
        client.run();
    }
}
