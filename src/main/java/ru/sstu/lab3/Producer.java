package ru.sstu.lab3;

import ru.sstu.lab3.abstractions.Buffer;

public class Producer<E> {
    public void produce(Buffer<E> buffer, E value){
        System.out.println("produced message: " + value);
        buffer.put(value);
    }
}
