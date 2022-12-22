package ru.sstu.lab1;

public class RunnableUtils {
    public static Runnable newSleepMillisTask(final long value) {
        return () -> {
            try {
                Thread.sleep(value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
