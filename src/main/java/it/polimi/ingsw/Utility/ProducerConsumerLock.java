package it.polimi.ingsw.Utility;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Implements Producer Consumer paradigm
 */
public class ProducerConsumerLock implements ReadWriteLock {
    private final ClassicLock readLock;
    private final ClassicLock writeLock;

    /**
     * Implements Producer Consumer paradigm using a couple of ClassicLocks
     *
     * @param readInitiallyLocked  read lock initially locked
     * @param writeInitiallyLocked write lock initially locked
     */
    public ProducerConsumerLock(boolean readInitiallyLocked, boolean writeInitiallyLocked) {
        readLock = new ClassicLock(readInitiallyLocked);
        writeLock = new ClassicLock(writeInitiallyLocked);
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }
}
