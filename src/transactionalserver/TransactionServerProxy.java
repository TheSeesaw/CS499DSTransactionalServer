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
public class TransactionServerProxy 
{
    int serverPort;
    int id;
    Socket serverSocket;
    ObjectInputStream fromServer;
    ObjectOutputStream toServer;
    
    public TransactionServerProxy(int aPort, int targetAcc)
    {
        this.serverPort = aPort;
        this.id = targetAcc;
    }
    
    public void openConnection()
    {
        try
        {
            this.serverSocket = new Socket("localhost", this.serverPort);
            this.fromServer = new ObjectInputStream(this.serverSocket.getInputStream());
            this.toServer = new ObjectOutputStream(this.serverSocket.getOutputStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void closeConnection()
    {
        try
        {
            this.toServer.flush();
            this.serverSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void write(Object parameter)
    {
        try
        {
            this.toServer.writeObject(parameter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Object read()
    {
        Object received = null;
        try
        {
            received = this.fromServer.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return received;
    }
}
