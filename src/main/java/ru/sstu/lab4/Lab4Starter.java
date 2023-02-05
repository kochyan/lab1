package ru.sstu.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Lab4Starter {
    private final static int capacity = 100;
    private final static int producersCount = 8;
    private final static int consumersCount = 8;
    private final static int totalMessages = 80;

    public static void main(String[] args) {
        Ring ring = new Ring(capacity);
        List<Thread> threads = new ArrayList<>(producersCount + consumersCount);

        IntStream.range(0, producersCount).forEach((num) -> {
            Thread writer = new Thread(() -> {
                for (int i = 0; i < (totalMessages / producersCount); i++) {
                    ring.write(Integer.valueOf(i).toString());
                }
            });
            writer.setPriority(10);
            writer.setName("writer-" + num);
            threads.add(writer);
        });

        IntStream.range(0, consumersCount).forEach((num) -> {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < (totalMessages / consumersCount); i++) {
                    ring.read();
                }
            });
            reader.setPriority(10);
            reader.setName("reader-" + num);
            threads.add(reader);
        });

        long totalBefore = System.currentTimeMillis();
        startAndWait(threads);
        long totalAfter = System.currentTimeMillis();

        System.out.println("\n\nВсего заняло " + (totalAfter - totalBefore) + " мс\n");
        ring.printTotalCount();
    }

    public static void startAndWait(List<Thread> threads) {
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
