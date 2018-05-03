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
public class AccountManager implements LockTypes {
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
    
    public int getAccountBalance(int accountID, LockManager lockManager, Transaction lockingTransaction, boolean enableLocking) {
        Account account = getAccount(accountID);
        if (enableLocking) {
            // get a read lock
            // NOTE: I don't know why this causes an error, or how to fix it
            lockManager.setLock(account, lockingTransaction, READ_LOCK);
        }
        return account.getBalance();
    }
    
    public void setAccountBalance(int accountID, int amount, LockManager lockManager, Transaction lockingTransaction, boolean enableLocking) {
        Account account = getAccount(accountID);
        if (enableLocking) {
            // get write lock
            lockManager.setLock(account, lockingTransaction, WRITE_LOCK);
        }
        account.setBalance(amount);
    }
}
