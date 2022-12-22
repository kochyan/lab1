package ru.sstu.lab3;

import ru.sstu.lab3.abstractions.Buffer;

import java.util.NoSuchElementException;

public class Consumer<E> {
    public void consume(Buffer<? extends E> buffer) throws NoSuchElementException {
        System.out.println("consumed message: " + buffer.get());
    }
}
