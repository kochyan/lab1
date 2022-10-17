/*
 Put header here
 */
package ru.sstu;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\nЗадание 1 - однопоточная обработка");
        task1(1000, 1, 5);
        System.out.println("\nЗадание 2 - многопоточная обработка c разделением листа на равные диапазоны. Сложность не зависит от индекса элемента");
        task2(1000, 3, 3);
        System.out.println("\nЗадание 3 - анализ эффективности многопоточной обработки c разделением листа на равные диапазоны. Сложность не зависит от индекса элемента");
        task3(1000, 10, 2);
        System.out.println("\nЗадание 4 - анализ эффективности многопоточной обработки c разделением листа на равные диапазоны. Сложность повышена, но не зависит от индекса элемента");
        task4(1000, 5, 5);
        System.out.println("\nЗадание 5 - анализ эффективности многопоточной обработки c разделением листа на равные диапазоны. Сложность увеличивается в зависимости от индекса элемента");
        task5(1000, 5, 3);
        System.out.println("\nЗадание 6 - анализ эффективности многопоточной обработки c круговым разделением листа. Сложность увеличивается в зависимости от индекса элемента");
        task6(1000, 5, 3);
    }

    public static void task1(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        System.out.println("элементов:\t" + elementsCount);
        System.out.println("потоков:\t" + threadsCount);
        final List<Long> list = new ArrayList<>();
        for (long i = 0; i < elementsCount; i++) {
            list.add(i);
        }
        for (int i = 0; i < iterationCount; i++) {
            ExecutorService executorService = new ThreadPoolExecutor(threadsCount, threadsCount, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(elementsCount));
            long timeBefore = System.currentTimeMillis();
            for (int j = 0; j < list.size(); j++) {
                executorService.submit(DummyRunnableUtils.newSleepMillisTask(1L));
            }
            executorService.shutdown();
            boolean isSuccess = executorService.awaitTermination(1L, TimeUnit.MINUTES);
            long timeAfter = System.currentTimeMillis();
            String message = isSuccess ?
                    "\tИтерация # " + (i + 1) + "\tУспешно завершена, обработка заняла: " + (timeAfter - timeBefore) + "мс"
                    : "\tИтерация # " + (i + 1) + "\tНеуспешна. Обработка заняла более 1 минуты";
            System.out.println(message);
        }
    }

    public static void task2(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        System.out.println("элементов:\t" + elementsCount);
        System.out.println("потоков:\t" + threadsCount);
        final List<Long> list = new ArrayList<>();
        for (long i = 0; i < elementsCount; i++) {
            list.add(i);
        }
        for (int i = 0; i < iterationCount; i++) {
            ExecutorService executorService = new ThreadPoolExecutor(threadsCount, threadsCount, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(elementsCount));
            int rangeSize = elementsCount / threadsCount;
            int lastCurrentRangeIndex = rangeSize == elementsCount ? rangeSize - 1 : rangeSize;
            int rangeNumber = 1;
            int elementsToHandle = 0;
            long timeBefore = System.currentTimeMillis();
            for (int j = 0; j < list.size(); j++) {
                elementsToHandle++;
                if (j == lastCurrentRangeIndex) {
                    if (rangeNumber < threadsCount) {
                        //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                        executorService.submit(DummyRunnableUtils.newSleepMillisTask(elementsToHandle * 1L));
                        rangeNumber++;
                        lastCurrentRangeIndex = rangeSize * rangeNumber;
                        elementsToHandle = 0;
                    }
                    if (rangeNumber == threadsCount) {
                        if (lastCurrentRangeIndex == (list.size() - 1) && elementsToHandle > 0) {
                            //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                            executorService.submit(DummyRunnableUtils.newSleepMillisTask(elementsToHandle * 1L));
                        } else {
                            lastCurrentRangeIndex = list.size() - 1;
                        }
                    }
                }
            }
            executorService.shutdown();
            boolean isSuccess = executorService.awaitTermination(5L, TimeUnit.MINUTES);
            long timeAfter = System.currentTimeMillis();
            String message = isSuccess ?
                    "\tИтерация # " + (i + 1) + "\tУспешно завершена, обработка заняла: " + (timeAfter - timeBefore) + "мс"
                    : "\tИтерация # " + (i + 1) + "\t Неуспешна. Обработка заняла более 1 минуты";
            System.out.println(message);
        }
    }

    public static void task3(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        task2(elementsCount, threadsCount, iterationCount);
    }

    public static void task4(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        System.out.println("элементов:\t" + elementsCount);
        System.out.println("потоков:\t" + threadsCount);
        final List<Long> list = new ArrayList<>();
        for (long i = 0; i < elementsCount; i++) {
            list.add(i);
        }
        for (int i = 0; i < iterationCount; i++) {
            ExecutorService executorService = new ThreadPoolExecutor(threadsCount, threadsCount, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(elementsCount));
            int rangeSize = elementsCount / threadsCount;
            int lastCurrentRangeIndex = rangeSize == elementsCount ? rangeSize - 1 : rangeSize;
            int rangeNumber = 1;
            int elementsToHandle = 0;
            long timeBefore = System.currentTimeMillis();
            for (int j = 0; j < list.size(); j++) {
                elementsToHandle++;
                if (j == lastCurrentRangeIndex) {
                    if (rangeNumber < threadsCount) {
                        //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                        executorService.submit(DummyRunnableUtils.newSleepMillisTask(elementsToHandle * 3L));
                        rangeNumber++;
                        lastCurrentRangeIndex = rangeSize * rangeNumber;
                        elementsToHandle = 0;
                    }
                    if (rangeNumber == threadsCount) {
                        if (lastCurrentRangeIndex == (list.size() - 1) && elementsToHandle > 0) {
                            //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                            executorService.submit(DummyRunnableUtils.newSleepMillisTask(elementsToHandle * 3L));
                        } else {
                            lastCurrentRangeIndex = list.size() - 1;
                        }
                    }
                }
            }
            executorService.shutdown();
            boolean isSuccess = executorService.awaitTermination(5L, TimeUnit.MINUTES);
            long timeAfter = System.currentTimeMillis();
            String message = isSuccess ?
                    "\tИтерация # " + (i + 1) + "\tУспешно завершена, обработка заняла: " + (timeAfter - timeBefore) + "мс"
                    : "\tИтерация # " + (i + 1) + "\t Неуспешна. Обработка заняла более 1 минуты";
            System.out.println(message);
        }
    }

    public static void task5(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        System.out.println("элементов:\t" + elementsCount);
        System.out.println("потоков:\t" + threadsCount);
        final List<Long> list = new ArrayList<>();
        for (long i = 0; i < elementsCount; i++) {
            list.add(i);
        }
        for (int i = 0; i < iterationCount; i++) {
            ExecutorService executorService = new ThreadPoolExecutor(threadsCount, threadsCount, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(elementsCount));
            int rangeSize = elementsCount / threadsCount;
            int lastCurrentRangeIndex = rangeSize == elementsCount ? rangeSize - 1 : rangeSize;
            int rangeNumber = 1;
            int elementsToHandle = 0;
            long timeBefore = System.currentTimeMillis();
            for (int j = 0; j < list.size(); j++) {
                elementsToHandle++;
                if (j == lastCurrentRangeIndex) {
                    if (rangeNumber < threadsCount) {
                        //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                        executorService.submit(DummyRunnableUtils.newSleepMillisTask((long) (elementsToHandle * Math.sqrt(j))));
                        rangeNumber++;
                        lastCurrentRangeIndex = rangeSize * rangeNumber;
                        elementsToHandle = 0;
                    }
                    if (rangeNumber == threadsCount) {
                        if (lastCurrentRangeIndex == (list.size() - 1) && elementsToHandle > 0) {
                            //System.out.println("\t\t\tВ диапазоне #" + rangeNumber + " " + elementsToHandle + " элементов");
                            executorService.submit(DummyRunnableUtils.newSleepMillisTask((long) (elementsToHandle * Math.sqrt(j))));
                        } else {
                            lastCurrentRangeIndex = list.size() - 1;
                        }
                    }
                }
            }
            executorService.shutdown();
            boolean isSuccess = executorService.awaitTermination(5L, TimeUnit.MINUTES);
            long timeAfter = System.currentTimeMillis();
            String message = isSuccess ?
                    "\tИтерация # " + (i + 1) + "\tУспешно завершена, обработка заняла: " + (timeAfter - timeBefore) + "мс"
                    : "\tИтерация # " + (i + 1) + "\t Неуспешна. Обработка заняла более 1 минуты";
            System.out.println(message);
        }
    }

    public static void task6(final int elementsCount, final int threadsCount, final int iterationCount) throws InterruptedException {
        System.out.println("элементов:\t" + elementsCount);
        System.out.println("потоков:\t" + threadsCount);
        final List<Long> list = new ArrayList<>();
        for (long i = 0; i < elementsCount; i++) {
            list.add(i);
        }
        for (int i = 0; i < iterationCount; i++) {
            ExecutorService executorService = new ThreadPoolExecutor(threadsCount, threadsCount, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(elementsCount));
            long timeBefore = System.currentTimeMillis();
            for (int j = 0; j < list.size(); j++) {
                executorService.submit(DummyRunnableUtils.newSleepMillisTask((long) Math.sqrt(j)));
            }
            executorService.shutdown();
            boolean isSuccess = executorService.awaitTermination(5L, TimeUnit.MINUTES);
            long timeAfter = System.currentTimeMillis();
            String message = isSuccess ?
                    "\tИтерация # " + (i + 1) + "\tУспешно завершена, обработка заняла: " + (timeAfter - timeBefore) + "мс"
                    : "\tИтерация # " + (i + 1) + "\tНеуспешна. Обработка заняла более 1 минуты";
            System.out.println(message);
        }
    }
}
