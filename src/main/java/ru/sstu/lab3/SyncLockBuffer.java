package ru.sstu.lab3;

import ru.sstu.lab3.abstractions.Buffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncLockBuffer<E> extends Buffer<E> {
    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();

    @Override
    public E get() {
        try {
            final boolean hasLock = readLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (hasLock) {
                E el = queue.poll();
                if (el == null) {
                    writeLock.notify();
                    readLock.wait(500);
                    el = queue.poll();
                }
                return el;
            }
        } catch (Exception ex) {
        } finally {
            readLock.unlock();
        }
        return null;
    }

    @Override
    public boolean put(E e) throws IllegalStateException {
        try {
            final boolean hasLock = writeLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (hasLock) {
                final boolean added = queue.offer(e);
                if (!added) {
                    writeLock.wait(500);
                    readLock.notify();
                    return queue.offer(e);
                }
            }
        } catch (Exception ex) {
        } finally {
            writeLock.unlock();
        }
        return false;
    }
}
