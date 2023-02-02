package ru.sstu.lab3;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lab3Starter {
    // commons
    private final static int bufferSize = 1;
    private final static int messageCount = 100_000;
    private final static List<Integer> messages = IntStream.range(0, messageCount).boxed().collect(Collectors.toUnmodifiableList());

    // case 1
    private final static AtomicInteger totalProduces1 = new AtomicInteger();
    private final static AtomicInteger totalConsumes1 = new AtomicInteger();
    private final static int consumersCount1 = 2;
    private final static int producersCount1 = 9;
    private final static BlockingQueue<Integer> buffer1 = new ArrayBlockingQueue<>(bufferSize);

    // case 2
    private final static AtomicInteger totalProduces2 = new AtomicInteger();
    private final static AtomicInteger totalConsumes2 = new AtomicInteger();
    private final static int consumersCount2 = 12;
    private final static int producersCount2 = 4;
    private final static BlockingQueue<Integer> buffer2 = new ArrayBlockingQueue<>(bufferSize);

    // case 3
    private final static AtomicInteger totalProduces3 = new AtomicInteger();
    private final static AtomicInteger totalConsumes3 = new AtomicInteger();
    private final static int consumersCount3 = 8;
    private final static int producersCount3 = 8;
    private final static Semaphore producerSemaphore = new Semaphore(1, true);
    private final static Semaphore consumerSemaphore = new Semaphore(0, true);
    private final static BlockingQueue<Integer> buffer3 = new ArrayBlockingQueue<>(bufferSize);

    // case 4
    private final static AtomicInteger totalProduces4 = new AtomicInteger();
    private final static AtomicInteger totalConsumes4 = new AtomicInteger();
    private final static int consumersCount4 = 4;
    private final static int producersCount4 = 12;
    private final static Object write = new Object();
    private final static Object read = new Object();
    private final static Lock writeLock = new ReentrantLock();
    private final static Lock readLock = new ReentrantLock();
    private final static BlockingQueue<Integer> buffer4 = new ArrayBlockingQueue<>(bufferSize);


    public static void main(String[] args) throws InterruptedException {
        //case1(producersCount1, consumersCount1);
        //case2(producersCount2, consumersCount2);
        case3(producersCount3, consumersCount3);
    }

    public static void case1(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("1. Взаимодействие читателей и писателей без средств синхронизации. \t\tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i <= producersCount; i++) {
            producers.submit(produce1(i));
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume1());
        }
        long before = System.currentTimeMillis();
        producers.shutdown();
        consumers.shutdown();
        producers.awaitTermination(5, TimeUnit.SECONDS);
        consumers.awaitTermination(5, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.printf("1. Всего заняло мс: %s\n Всего успешных записей в буфер: %s, успешных чтений из буфера %s", (after - before - 300), totalProduces1.get(), totalConsumes1.get());
    }

    public static Runnable produce1(final int startIdx) {
        return () -> {
            try {
                int currentIdx = startIdx;
                while (currentIdx < messageCount) {
                    Integer val = messages.get(currentIdx);
                    System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    buffer1.add(val);
                    totalProduces1.incrementAndGet();
                    currentIdx += producersCount1;
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable consume1() {
        return () -> {
            try {
                while (true) {
                    Integer val = buffer1.poll(300, TimeUnit.MILLISECONDS);
                    if (val != null) {
                        System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                        totalConsumes1.incrementAndGet();
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static void case2(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("\n2. Взаимодействие читателей и писателей со средствами синхронизации: thread-safe коллекция. \tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce2(i));
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume2());
        }
        long before = System.currentTimeMillis();
        producers.shutdown();
        consumers.shutdown();
        producers.awaitTermination(5, TimeUnit.SECONDS);
        consumers.awaitTermination(5, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.printf("2. Всего заняло мс: %s\n Всего успешных записей в буфер: %s, успешных чтений из буфера %s\n Потоков читателей: %s, Потоков писателей: %s",
                (after - before - 300), totalProduces2.get(), totalConsumes2.get(), consumersCount2, producersCount2);
    }

    public static Runnable produce2(final int startIdx) {
        return () -> {
            try {
                int currentIdx = startIdx;
                while (currentIdx < messageCount) {
                    Integer val = messages.get(currentIdx);
                    boolean successAdded = buffer2.offer(val, 300, TimeUnit.MILLISECONDS);
                    System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    if (successAdded) {
                        totalProduces2.incrementAndGet();
                        currentIdx += producersCount2;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable consume2() {
        return () -> {
            try {
                while (true) {
                    Integer val = buffer2.poll(300, TimeUnit.MILLISECONDS);
                    if (val != null) {
                        System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                        totalConsumes2.incrementAndGet();
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static void case3(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("3. Взаимодействие читателей и писателей при помощи Semaphore. \t\tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce3(i));
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume3());
        }
        long before = System.currentTimeMillis();
        producers.shutdown();
        consumers.shutdown();
        producers.awaitTermination(5, TimeUnit.SECONDS);
        consumers.awaitTermination(5, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.printf("3. Всего заняло мс: %s\n Всего успешных записей в буфер: %s, успешных чтений из буфера %s\n Потоков читателей: %s, Потоков писателей: %s",
                (after - before - 300), totalProduces3.get(), totalConsumes3.get(), consumersCount3, producersCount3);
    }

    public static Runnable produce3(final int startIdx) {
        return () -> {
            int currentIdx = startIdx;
            while (currentIdx < messageCount) {
                try {
                    final boolean acquired = producerSemaphore.tryAcquire(1, 3, TimeUnit.SECONDS);
                    if (acquired) {
                        try {
                            Integer val = messages.get(currentIdx);
                            buffer3.add(val);
                            System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                            totalProduces3.incrementAndGet();
                            currentIdx += producersCount3;
                        } finally {
                            consumerSemaphore.release();
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
                }
            }
        };
    }

    public static Runnable consume3() {
        return () -> {
            try {
                while (true) {
                    final boolean acquired = consumerSemaphore.tryAcquire(1, 300, TimeUnit.MILLISECONDS);
                    if (acquired) {
                        try {
                            Integer val = buffer3.poll(100, TimeUnit.MILLISECONDS);
                            if (val != null) {
                                System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                                totalConsumes3.incrementAndGet();
                            }
                        } finally {
                            producerSemaphore.release(1);
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }
/*
    public static void case4(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("4. Взаимодействие читателей и писателей при помощи lock. \t\tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce41(i));
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume41());
        }
        long before = System.currentTimeMillis();
        consumers.shutdown();
        producers.shutdown();
        producers.awaitTermination(5, TimeUnit.SECONDS);
        consumers.awaitTermination(5, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.printf("4. Всего заняло мс: %s\n Всего успешных записей в буфер: %s, успешных чтений из буфера %s\n Потоков читателей: %s, Потоков писателей: %s",
                (after - before - 300), totalProduces4.get(), totalConsumes4.get(), consumersCount4, producersCount4);
    }

    public static Runnable produce4(final int startIdx) {
        return () -> {
            int currentIdx = startIdx;
            while (currentIdx < messageCount) {
                try {
                    boolean writeLocked = writeLock.tryLock(100, TimeUnit.MILLISECONDS);
                    if (writeLocked) {
                        //boolean readLocked = readLock.tryLock(100, TimeUnit.MILLISECONDS);
                        //if (readLocked) {
                        Integer val = messages.get(currentIdx);
                        while (buffer4.size() == 0) {
                            System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                            buffer4.add(val);

                            totalProduces4.incrementAndGet();
                            currentIdx += producersCount4;
                        }
                        //readLock.unlock();
                        //}
                    }
                } catch (Exception e) {
                    System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
                    writeLock.unlock();
                }
            }
        };
    }

    public static Runnable consume4() {
        return () -> {
            try {
                while (true) {
                    //boolean writeLocked = writeLock.tryLock(100, TimeUnit.MILLISECONDS);
                    boolean readLocked = readLock.tryLock(100, TimeUnit.MILLISECONDS);
                    if (readLocked) {
                        Integer val = buffer4.poll(100, TimeUnit.MILLISECONDS);
                        if (val != null) {
                            System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                            totalConsumes4.incrementAndGet();
                        } else {
                            break;
                        }
                    }
                    readLock.unlock();
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
                readLock.unlock();
            }
        };
    }

    public static Runnable produce41(final int startIdx) {
        return () -> {
            int currentIdx = startIdx;
            synchronized (read) {
                if (buffer4.size() == 0) {
                    Integer val = messages.get(currentIdx);
                    System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    buffer4.add(val);
                    totalProduces4.incrementAndGet();
                    currentIdx += producersCount4;
                    read.notify();
                }
            }
            while (currentIdx < messageCount) {
                try {
                    synchronized (write){
                        write.wait();
                    }
                    Integer val = messages.get(currentIdx);
                    buffer4.add(val);
                    System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    totalProduces4.incrementAndGet();
                    currentIdx += producersCount4;
                    read.notify();
                } catch (Exception e) {
                    //System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
                    e.printStackTrace();
                } finally {
                    System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
                }
            }
        };
    }

    public static Runnable consume41() {
        return () -> {
            try {
                while (true) {
                    synchronized (read){
                        read.wait();
                    }
                    Integer val = buffer4.poll(100, TimeUnit.MILLISECONDS);
                    if (val != null) {
                        System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                        totalConsumes4.incrementAndGet();
                        write.notify();
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.printf("Exception in thread [%s]: %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

 */

}