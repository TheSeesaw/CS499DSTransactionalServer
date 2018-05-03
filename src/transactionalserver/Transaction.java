/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

/**
 *
 * @author Krasis
 */
public class Transaction 
{
    int id;
    String log;
    
    public Transaction(int id) {
        this.id = id;
        this.log = "";
    }
    
    public void log(String msg) {
        this.log += msg;
    }
}
