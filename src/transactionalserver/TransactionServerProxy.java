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
public class TransactionServerProxy implements MessageTypes
{
    int serverPort;
    String serverHost;
    int id;
    Socket serverSocket;
    ObjectInputStream fromServer;
    ObjectOutputStream toServer;
    
    public TransactionServerProxy(int aPort, String aHost)
    {
        this.serverPort = aPort;
        this.serverHost = aHost;
    }
    
    public void openConnection()
    {
        Message msg = new Message(OPEN_TRANSACTION, null); 
        try
        {
            this.serverSocket = new Socket(this.serverHost, this.serverPort);
            this.fromServer = new ObjectInputStream(this.serverSocket.getInputStream());
            this.toServer = new ObjectOutputStream(this.serverSocket.getOutputStream());
            // write open request to server to get back transaction id
            this.toServer.writeObject(msg);
            // read back transaction ID
            Message openConfirm = (Message)this.fromServer.readObject();
            this.id = (Integer)openConfirm.getContent();
            System.out.println("[TransactionServerProxy.openConnection] New transaction created with id: " + this.id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void closeConnection()
    {
        Message msg = new Message(CLOSE_TRANSACTION, null);
        try
        {
            this.toServer.writeObject(msg);
            this.toServer.flush();
            this.serverSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("[TransactionServerProxy.openConnection] Transaction with id: " + this.id + " closed.");
    }
    
    public void write(int targetAccount, int parameter)
    {
        Object[] writeContents = new Object[2];
        writeContents[0] = targetAccount;
        writeContents[1] = parameter;
        Message msg = new Message(WRITE_REQUEST, writeContents);
        try
        {
            this.toServer.writeObject(msg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Object read(int targetAccount)
    {
        Message msg = new Message(READ_REQUEST, targetAccount);
        Message received = null;
        try
        {
            this.toServer.writeObject(msg);
            received = (Message)this.fromServer.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return received.getContent();
    }
}
