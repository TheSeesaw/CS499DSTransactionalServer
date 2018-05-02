/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

import java.io.*;
import java.net.*;
/**
 *
 * @author Krasis
 */
public class TransactionalServer implements MessageTypes
{
    public static int serverPort;
    public static ServerSocket socket = null;
    public static Transaction transactions = null;
    
    
    public TransactionalServer(int serverPort)
    {
        this.serverPort = serverPort;
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
                // read message
                this.message = (Message)fromClient.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // handle message based on type
            switch (message.getType()) {
                case OPEN_TRANSACTION:
                    break;
                case CLOSE_TRANSACTION:
                    break;
                case READ_REQUEST:
                    break;
                case WRITE_REQUEST:
                    break;
                default:
                    System.err.println("[TransactionWorkerThread.run] Warning: Message type not implemented");
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
        TransactionalServer server = new TransactionalServer(serverPort);
        server.run();
    }
    
}
