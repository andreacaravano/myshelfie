package it.polimi.ingsw.Utility;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Classic Lock implementation
 */

public class ClassicLock implements Lock {
    private final Semaphore mutex;

    /**
     * Classic Lock implementation, using a non re-entrant strategy
     *
     * @param initiallyLocked true if the lock has to be initially locked
     */
    public ClassicLock(boolean initiallyLocked) {
        if (initiallyLocked)
            mutex = new Semaphore(0);
        else mutex = new Semaphore(1);
    }

    @Override
    public void lock() {
        try {
            mutex.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mutex.acquire(1);
    }

    @Override
    public boolean tryLock() {
        return mutex.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mutex.tryAcquire(time, unit);
    }

    @Override
    public void unlock() {
        mutex.release(1);
    }

    @Override
    public Condition newCondition() {
        // not interesting for our implementation
        return null;
    }
}
