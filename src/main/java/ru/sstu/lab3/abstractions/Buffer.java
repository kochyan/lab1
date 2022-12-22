package ru.sstu.lab3.abstractions;

import java.util.*;

public abstract class Buffer<E> {
    private static final int DEFAULT_CAPACITY = 1;
    protected final Queue<E> queue = new PriorityQueue<>(DEFAULT_CAPACITY);

    public abstract E get() throws NoSuchElementException;
    public abstract boolean put(E e) throws IllegalStateException;
}
