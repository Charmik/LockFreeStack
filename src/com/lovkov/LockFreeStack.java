package com.lovkov;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Charm on 24/10/2016.
 */
public class LockFreeStack<T> {

    AtomicReference<Node> head = new AtomicReference<>();

    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        Node node;
        do {
            newNode.next = node = head.get();
        } while (!head.compareAndSet(node, newNode));
    }

    public T pop() {
        Node next;
        Node node;
        do {
            node = head.get();
            if (node == null) {
                throw new NoSuchElementException();
            }
            next = node.next;
        } while (!head.compareAndSet(node, next));
        return (T) node.value;
    }


    private class Node<T> {
        T value;
        Node next;

        public Node(T value) {
            this.value = value;
        }
    }

}
