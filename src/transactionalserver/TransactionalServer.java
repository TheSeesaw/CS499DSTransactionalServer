/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
/**
 *
 * @author Krasis
 */
public class TransactionalServer implements MessageTypes
{
    public static int serverPort;
    public static ServerSocket socket = null;
    public static ArrayList<Transaction> transactions = null;
    public static int numTransactions = 0;
    public static int remainingTransactions = 0;
    public static AccountManager accManager = null;
    public static LockManager lockManager = null;
    public static boolean useLocking;
    
    
    public TransactionalServer(int serverPort, int numAccounts, int numTransactions, boolean locking)
    {
        TransactionalServer.serverPort = serverPort;
        // create a socket for the server
        try
        {
            System.out.println("Starting server . . .");
            TransactionalServer.socket = new ServerSocket(TransactionalServer.serverPort);
        }
        catch (IOException e)
        {
            System.out.println("Unable to start server listening on port: " + Integer.toString(TransactionalServer.serverPort));
            e.printStackTrace();
            System.exit(-1);
        }
        TransactionalServer.accManager = new AccountManager(numAccounts);
        TransactionalServer.lockManager = new LockManager();
        TransactionalServer.transactions = new ArrayList<>(numTransactions);
        TransactionalServer.useLocking = locking;
        
    }
    
    public void closeServer() {
        if (remainingTransactions == 0) {
            
        }
    }
    
    public void run()
    {
        while (true) {
            System.out.println("[TransactionServer.run] Waiting for connections on Port " + Integer.toString(TransactionalServer.serverPort));
            try 
            {
                TransactionWorkerThread wThread = new TransactionWorkerThread(TransactionalServer.socket.accept());
                System.out.println("[TransactionServer.run] A connection to a client is established!");
                wThread.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class TransactionWorkerThread extends Thread
    {
        Socket client = null;
        ObjectInputStream fromClient = null;
        ObjectOutputStream toClient = null;
        Message message = null;
        Message response = null;
        boolean alive = true;
        Transaction transaction = null;
        
        private TransactionWorkerThread(Socket client) 
        {
            this.client = client;
        }
        
        public void run()
        {
            // create reader and writer for client, then take message
            try {
                this.toClient = new ObjectOutputStream(this.client.getOutputStream());
                this.fromClient = new ObjectInputStream(this.client.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(this.alive)
            {
                // get a message from the network
                try {
                    this.message = (Message)fromClient.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // handle message based on type
                switch (message.getType()) {
                    case OPEN_TRANSACTION:
                        // creat new transaction object
                        synchronized(TransactionalServer.transactions) {
                            this.transaction = new Transaction(TransactionalServer.numTransactions);
                            TransactionalServer.numTransactions++;
                            TransactionalServer.transactions.add(this.transaction);
                        }
                        // write transaction id back to client
                        response = new Message(OPEN_TRANSACTION, this.transaction.id);
                        try {
                            this.toClient.writeObject(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                        
                    case CLOSE_TRANSACTION:
                        synchronized(TransactionalServer.transactions) {
                            TransactionalServer.transactions.remove(this.transaction);
                        }
                        try {
                            this.fromClient.close();
                            this.toClient.close();
                            this.client.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        this.alive = false;
                        break;
                        
                    case READ_REQUEST:
                        // read the target account balance
                        int readResult = TransactionalServer.accManager.getAccountBalance((Integer)message.getContent(), TransactionalServer.lockManager, this.transaction, TransactionalServer.useLocking);
                        System.out.println("[TransactionWorkerThread.run] reading balance from account: " + (Integer)message.getContent());
                        response = new Message(READ_REQUEST, readResult);
                        // write the balance back to the client
                        try {
                            this.toClient.writeObject(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                        
                    case WRITE_REQUEST:
                        // get target account and new balance from message
                        Object[] writeData = (Object[])message.getContent();
                        int targetAcc = (Integer)writeData[0];
                        int newBalance = (Integer)writeData[1];
                        System.out.println("[TransactionWorkerThread.run] writing balance: " + newBalance + " to account: " + targetAcc);
                        // write the balance to the target account
                        TransactionalServer.accManager.setAccountBalance(targetAcc, newBalance, TransactionalServer.lockManager, this.transaction, TransactionalServer.useLocking);
                        break;
                        
                    default:
                        System.err.println("[TransactionWorkerThread.run] Warning: Message type not implemented");
                }
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // create a new server object
        int serverPort = 8080; // Integer.parseInt(args[0]);
        int accParam = 10; //Integer.parseInt(args[1]);
        int transParam = 10; //Integer.parseInt(args[2]);
        TransactionalServer server = new TransactionalServer(serverPort, accParam, transParam, true);
        server.run();
    }
    
}
