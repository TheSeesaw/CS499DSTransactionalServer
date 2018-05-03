/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

import java.util.ArrayList;
/**
 *
 * @author Krasis
 */
public class AccountManager {
    ArrayList<Account> accounts;
    
    public AccountManager(int numAccounts) {
        this.accounts = new ArrayList<>(numAccounts);
        for (; numAccounts > 0; numAccounts--) {
            this.accounts.add(new Account(10));
        }
    }
    
    public Account getAccount(int accountID) {
        return this.accounts.get(accountID);
    }
    
    public int getAccountBalance(int accountID) {
        // TODO: get read lock
        Account account = getAccount(accountID);
        return account.getBalance();
    }
    
    public void setAccountBalance(int accountID, int amount) {
        // TODO get write lock
        Account account = getAccount(accountID);
        account.setBalance(amount);
    }
}
