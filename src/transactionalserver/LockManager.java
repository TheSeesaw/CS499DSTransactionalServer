/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

import java.util.Hashtable;
import java.util.Enumeration;
/**
 *
 * @author Krasis
 */
public class LockManager implements LockTypes {
    private Hashtable<Account,Lock> locks;
    
    public LockManager() {
        this.locks = new Hashtable<>();
    }
    
    public Lock getAccountLock(Account targetAccount) {
        Lock lockExists = locks.get(targetAccount);
        return lockExists;
    }
    
    public void setLock(Account targetAccount, Transaction lockingTransaction, LockTypes type) {
        Lock foundLock;
        synchronized(this) {
            foundLock = this.getAccountLock(targetAccount);
            if (foundLock == null) {
                foundLock = new Lock();
                this.locks.put(targetAccount, foundLock);
            }
        }
        foundLock.acquire(lockingTransaction, type);
    }
    
    public synchronized void unLock(Transaction unlockingTransaction) {
        Enumeration lockElements = this.locks.elements();
        while(lockElements.hasMoreElements()) {
            Lock aLock = (Lock)(lockElements.nextElement());
            for (int i = 0; i < aLock.holdingTransactions.size(); i++) {
                if (aLock.holdingTransactions.get(i) == unlockingTransaction.id) {
                    aLock.release(unlockingTransaction);
                }
            }
        }
    }
}
