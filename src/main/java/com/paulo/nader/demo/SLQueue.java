
package com.paulo.nader.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SLQueue<T> {

    private class Node {
        private T data;
        private Node next;

        public Node(T data) {
            this.data = data;
            next = null;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public SLQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Adds an element to the end of the queue
    public void enqueue(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }

        Node newNode = new Node(obj);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        }
        else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    // Removes and returns the front element
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        T removed = front.data;
        front = front.next;
        size--;

        if (front == null) {
            rear = null;
        }

        return removed;
    }

    // Returns the front element without removing it
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return front.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // Removes all elements from the queue
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    // Checks whether an element is in the queue
    public boolean contains(T obj) {
        Node current = front;

        while (current != null) {
            if (current.data.equals(obj)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    // Removes the first occurrence of an element
    public boolean remove(T obj) {
        if (obj == null || isEmpty()) {
            return false;
        }

        if (front.data.equals(obj)) {
            dequeue();
            return true;
        }

        Node current = front;

        while (current.next != null) {
            if (current.next.data.equals(obj)) {
                if (current.next == rear) {
                    rear = current;
                }

                current.next = current.next.next;
                size--;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    // Removes an element at a given position (zero-based)
    public boolean remove(int position) {
        if (position < 0 || position >= size) {
            return false;
        }

        if (position == 0) {
            dequeue();
            return true;
        }

        Node current = front;

        for (int i = 0; i < position - 1; i++) {
            current = current.next;
        }

        if (current.next == rear) {
            rear = current;
        }

        current.next = current.next.next;
        size--;

        return true;
    }

    // Returns the queue as a list in FIFO order
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Node current = front;

        while (current != null) {
            result.add(current.data);
            current = current.next;
        }

        return result;
    }

    // Returns the zero-based position of an element, or -1 if not found
    public int getPosition(T obj) {
        Node current = front;
        int position = 0;

        while (current != null) {
            if (current.data.equals(obj)) {
                return position;
            }

            current = current.next;
            position++;
        }

        return -1;
    }
}
