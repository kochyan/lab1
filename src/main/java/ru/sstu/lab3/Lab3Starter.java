package ru.sstu.lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lab3Starter {
    final static int bufferSize = 1;
    final static int consumersCount1 = 2;
    final static int producersCount1 = 6;
    final static int consumersCount2 = 4;
    final static int producersCount2 = 12;
    final static int consumersCount3 = 3;
    final static int producersCount3 = 9;
    final static List<Integer> buffer1 = new ArrayList<>(bufferSize);
    final static BlockingQueue<Integer> buffer2 = new ArrayBlockingQueue<>(bufferSize);

    final static List<Integer> buffer3 = new ArrayList<>(bufferSize);
    final static CountDownLatch producersFinished1 = new CountDownLatch(producersCount1);
    final static CountDownLatch producersFinished2 = new CountDownLatch(producersCount2);
    final static CountDownLatch producersFinished3 = new CountDownLatch(producersCount3);
    final static AtomicBoolean consumersTurn3 = new AtomicBoolean(false);
    final static Semaphore producerSemaphore = new Semaphore(1, true);
    final static Semaphore consumerSemaphore = new Semaphore(0, true);

    public static void main(String[] args) throws InterruptedException {
        //case1(producersCount1, consumersCount1);
        //case2(producersCount2, consumersCount2);
        case3(producersCount3, consumersCount3);
    }

    public static void case1(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("1. Взаимодействие читателей и писателей без средств синхронизации. \t\tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce1());
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume1());
        }
        long before = System.currentTimeMillis();
        producers.shutdown();
        consumers.shutdown();
        producers.awaitTermination(1, TimeUnit.SECONDS);
        consumers.awaitTermination(1, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.println("1. Всего заняло мс: " + (after - before));
    }

    public static void case2(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("\n2. Взаимодействие читателей и писателей со средствами синхронизации: thread-safe коллекция. \tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce2());
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
        System.out.println("2. Всего заняло мс: " + (after - before));
    }

    public static void case3(int producersCount, int consumersCount) throws InterruptedException {
        System.out.printf("3. Взаимодействие читателей и писателей при помощи Semaphore. \t\tчитателей: [%d]\tписателей: [%d]\n", consumersCount, producersCount);
        final ExecutorService consumers = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService producers = Executors.newFixedThreadPool(producersCount);
        for (int i = 0; i < producersCount; i++) {
            producers.submit(produce3());
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers.submit(consume3());
        }
        long before = System.currentTimeMillis();
        producers.shutdown();
        consumers.shutdown();
        producers.awaitTermination(1, TimeUnit.SECONDS);
        consumers.awaitTermination(1, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.println("3. Всего заняло мс: " + (after - (before + 1000)));
    }

    public static Runnable produce1() {
        return () -> {
            try {
                final int val = new Random().nextInt(1000);
                System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                buffer1.add(0, val);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                producersFinished1.countDown();
                System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable consume1() {
        return () -> {
            while (producersFinished1.getCount() > 0) {
                try {
                    final int val = buffer1.get(0);
                    System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                } finally {
                    System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
                }
            }
        };
    }

    public static Runnable produce2() {
        return () -> {
            try {
                final int val = new Random().nextInt(1000);
                System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                buffer2.put(val);
            } catch (InterruptedException e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                producersFinished2.countDown();
                System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable consume2() {
        return () -> {
            try {
                while (producersFinished2.getCount() > 0) {
                    Integer val = buffer2.poll(3, TimeUnit.SECONDS);
                    if (val != null) {
                        System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
            }
        };
    }

    public static Runnable produce3() {
        return () -> {
            try {
                final boolean acquired = producerSemaphore.tryAcquire(1, 10, TimeUnit.SECONDS);
                if (acquired) {
                    final int val = new Random().nextInt(1000);
                    System.out.printf("produced value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    buffer3.add(0, val);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                producersFinished3.countDown();
                System.out.printf("\tproducer-thread: [%s] finished\n", Thread.currentThread().getName());
                consumerSemaphore.release();
            }
        }

                ;
    }

    public static Runnable consume3() {
        return () -> {
            while (producersFinished3.getCount() > 0) {
                try {
                    final boolean acquired = consumerSemaphore.tryAcquire(1, 10, TimeUnit.SECONDS);
                    if (acquired) {
                        final int val = buffer3.get(0);
                        System.out.printf("consumed value: [%d] by thread: [%s]\n", val, Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                } finally {
                    System.out.printf("\tconsumer-thread: [%s] finished\n", Thread.currentThread().getName());
                    producerSemaphore.release();
                }
            }
        };
    }
}