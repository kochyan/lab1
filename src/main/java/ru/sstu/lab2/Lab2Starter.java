package ru.sstu.lab2;


public class Lab2Starter {
    public static void main(String[] args) {
        case1(10_000);
        case2(10_000, 10);
    }

    public static void case1(int numbers) {
        Eratosfen eratosfen = new Eratosfen(numbers);
        long before = System.currentTimeMillis();
        eratosfen.fillSieve();
        long after = System.currentTimeMillis();
        eratosfen.printCount();
        System.out.printf("\nПоиск простых чисел из %d элементов при помощи 1 потока занял %d мс\n\n", numbers, (after - before));
    }

    public static void case2(int numbers, int concurrency) {
        Eratosfen eratosfen = new Eratosfen(numbers);
        long before = System.currentTimeMillis();
        eratosfen.concurrentFillSieve(concurrency);
        long after = System.currentTimeMillis();
        eratosfen.printCount();
        System.out.printf("\nПоиск простых чисел из %d элементов при помощи %d потоков занял %d мс\n", numbers, concurrency, (after - before));
    }
}
