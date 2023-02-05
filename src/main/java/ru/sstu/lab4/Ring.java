package ru.sstu.lab4;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Ring {
    private final Object writeLock = new Object();
    private final Object readLock = new Object();
    private final AtomicBoolean full = new AtomicBoolean(false);
    private final AtomicBoolean empty = new AtomicBoolean(true);
    private Queue<String> buffer;
    private final int capacity;

    private final AtomicInteger totalWriteCount = new AtomicInteger(0);
    private final AtomicInteger totalReadCount = new AtomicInteger(0);


    public Ring(int capacity) {
        synchronized (this) {
            this.buffer = new LinkedList<>();
            this.capacity = capacity;
        }
    }

    public void write(String msg) {
        long before = System.currentTimeMillis();
        synchronized (writeLock) {
            if (full.get()) {
                System.out.println("buffer is full...");
                while (full.get()) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            buffer.add(msg);
            totalWriteCount.incrementAndGet();
            long after = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " write msg: " + msg + ", it took " + (after - before) + " мс");
            empty.set(false);
            if (buffer.size() == capacity) {
                full.set(true);
            }
        }
    }

    public void read() {
        long before = System.currentTimeMillis();
        synchronized (readLock) {
            String msg;
            if (empty.get()) {
                System.out.println("buffer is empty...");
                while (empty.get()) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            msg = buffer.poll();
            totalReadCount.incrementAndGet();
            long after = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " read msg: " + msg + ", it took " + (after - before) + " мс");
            full.set(false);
            if (buffer.isEmpty()) {
                empty.set(true);
            }
        }
    }

    public void printTotalCount() {
        System.out.println("Всего прочитано: " + totalReadCount.get() + "\t Всего записано: " + totalWriteCount.get());
    }
}