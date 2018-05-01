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
    static ArrayList<TransactionServerProxy> proxies;
    static Random rGen;
    
    public TransactionalClient(int aPort, int accounts, int transactions)
    {
        TransactionalClient.serverPort = aPort;
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
        int initialBalance;
        int endBalance;
        int tempValue;
        int randValue;
        TransactionServerProxy proxy;
        
        private TransactionServerProxyThread(int transactionID)
        {
            this.id = transactionID;
            // create a proxy to work through and add it to known proxies
            this.proxy = new TransactionServerProxy(this.id, TransactionalClient.serverPort);
            TransactionalClient.proxies.add(this.proxy);
            // open a connection to the server
            this.proxy.openConnection();
            // read account value from the server
            initialBalance = (Integer)this.proxy.read();
            // modify account by random value between 1 and 10
            randValue = TransactionalClient.rGen.nextInt(10) + 1;
            tempValue = initialBalance - randValue;
            // write account value back to the server
            this.proxy.write(tempValue);
            // close the connection
            this.proxy.closeConnection();
        }
    }
    
    public static void main(String[] args) 
    {
        // get number of accounts and transactions from the command line
        // NOTE: hardcoded for now
        int serverPort = 8080; // Integer.parseInt(args[0]);
        int accParam = 10; //Integer.parseInt(args[1]);
        int transParam = 10; //Integer.parseInt(args[0]);
        
        // create a new client
        TransactionalClient client = new TransactionalClient(serverPort, accParam, transParam);
        client.run();
    }
}
