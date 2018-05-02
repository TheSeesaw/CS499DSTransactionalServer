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
public class Account {
    int balance;
    
    public Account(int startingBalance) 
    {
        this.balance = startingBalance;
    }
    
    public int getBalance()
    {
        return this.balance;
    }
    
    public void setBalance(int newBalance)
    {
        this.balance = newBalance;
    }
}
