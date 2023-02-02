package ru.sstu.lab2;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Eratosfen {
    private boolean[] primes;

    public Eratosfen(int n) {
        primes = new boolean[n + 1];
        Arrays.fill(primes, true);
        primes[0] = false;
        primes[1] = false;
    }

    public void fillSieve() {
        int to = primes.length;
        for (int i = 2; i < to; ++i) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
            }
            if (primes[i]) {
                for (int j = 2; i * j < to; ++j) {
                    primes[i * j] = false;
                }
            }
        }
    }

    public void concurrentFillSieve(final int concurrency) {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        AtomicInteger integer = new AtomicInteger(0);
        int to = primes.length;
        int part = to / concurrency;

        for (int threadNumber = 0; threadNumber < concurrency; threadNumber++) {
            executorService.submit(() -> {
                int start;
                int end;
                int partNumber = integer.incrementAndGet();
                if (partNumber == concurrency) {
                    start = (partNumber - 1) * part;
                    end = to;
                } else if (partNumber == 1) {
                    start = 2;
                    end = part;
                } else {
                    start = (partNumber - 1) * part;
                    end = (partNumber * part);
                }

                long before = System.currentTimeMillis();
                for (int i = start; i < end; ++i) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                    }
                    if (primes[i]) {
                        for (int j = start; i * j < to; ++j) {
                            primes[i * j] = false;
                        }
                    }
                    //System.out.printf("%s from:%d to:%d\n", Thread.currentThread().getName(), start, end);
                }
                long after = System.currentTimeMillis();
                System.out.println(Thread.currentThread().getName() + " " + (after - before) + " мс");
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    public void printCount() {
        int counter = 0;
        for (int i = 0; i < primes.length; i++) {
            if (primes[i]) {
                counter++;
            }
        }
        System.out.print("Всего простых чисел: " + counter);
    }

    public void printResult() {
        System.out.print("Простые числа: ");
        for (int i = 0; i < primes.length; i++) {
            if (primes[i]) {
                System.out.print(i + " ");
            }
        }
    }
}
