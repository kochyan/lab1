package ru.sstu.lab3;

import ru.sstu.lab3.abstractions.Buffer;

import java.util.NoSuchElementException;

public class UnsyncBuffer<E> extends Buffer<E> {
    @Override
    public E get() throws NoSuchElementException {
        return queue.poll();
    }

    @Override
    public boolean put(E e) throws IllegalStateException {
        return queue.add(e);
    }
}
