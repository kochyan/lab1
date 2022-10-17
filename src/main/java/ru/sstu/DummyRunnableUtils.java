package ru.sstu;

public class DummyRunnableUtils {
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
