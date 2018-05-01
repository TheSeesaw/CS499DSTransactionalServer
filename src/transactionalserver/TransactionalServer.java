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
public class TransactionalServer 
{
    ServerSocket socket = null;
    
    
    public TransactionalServer()
    {
        // create a socket for the server
        try
        {
            System.out.println("Starting server . . .");
            this.socket = new ServerSocket(8080);
        }
        catch (IOException e)
        {
            System.out.println("Unable to start server listening on port: 8080");
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    public void run()
    {
        while (true) {
            System.out.println("[TransactionServer.run] Waiting for connections on Port #8080");
            try 
            {
                TransactionWorkerThread wThread = new TransactionWorkerThread(this.socket.accept());
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
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        //Message message = null;
        
        private TransactionWorkerThread(Socket client) 
        {
            this.client = client;
        }
        
        public void run()
        {
            
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // create a new server object
        TransactionalServer server = new TransactionalServer();
        server.run();
    }
    
}
