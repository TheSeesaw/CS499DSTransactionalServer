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
public class Lock implements LockTypes {
    public Account lockedAccount;
    public ArrayList<Integer> holdingTransactions;
    public LockTypes lockType;
    
    public synchronized void acquire(Transaction lockingTransaction, LockTypes aLockType) {
        while(!this.holdingTransactions.isEmpty() && this.lockType.equals(WRITE_LOCK)) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        if (this.holdingTransactions.isEmpty()) {
            this.holdingTransactions.add(lockingTransaction.id);
            this.lockType = aLockType;
        } else if (!this.holdingTransactions.isEmpty() && this.lockType.equals(READ_LOCK)) {
            // check if this transaction already holds a read lock
            if (this.holdingTransactions.indexOf(lockingTransaction.id) == -1) {
                // lock not already held, add this transaction to holders
                this.holdingTransactions.add(lockingTransaction.id);
            }
        } else if (this.holdingTransactions.indexOf(lockingTransaction.id) != -1 && aLockType.equals(WRITE_LOCK)) {
            // this transaction needs to promote the lock
            this.lockType = aLockType;
        }
    }
    
    public synchronized void release(Transaction unlockingTransaction) {
        this.holdingTransactions.remove(unlockingTransaction.id);
        this.lockType = null;
        notifyAll();
    }
}
