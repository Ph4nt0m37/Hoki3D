
package com.paulo.nader.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A singly linked FIFO queue that supports removal and lookup by value or
 * position.
 *
 * @param <T> the type of elements stored in the queue
 */
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

    /**
     * Creates an empty queue.
     */
    public SLQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Adds an element to the end of the queue
    /**
     * Adds an element to the rear of the queue.
     *
     * @param obj the element to add
     * @throws IllegalArgumentException if {@code obj} is {@code null}
     */
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
    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return the element removed from the queue
     * @throws NoSuchElementException if the queue is empty
     */
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
    /**
     * Returns the element at the front of the queue without removing it.
     *
     * @return the front element
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return front.data;
    }

    /**
     * Indicates whether the queue contains no elements.
     *
     * @return {@code true} if the queue is empty; otherwise, {@code false}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the number of elements
     */
    public int size() {
        return size;
    }

    // Removes all elements from the queue
    /**
     * Removes all elements from the queue.
     */
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    // Checks whether an element is in the queue
    /**
     * Checks whether the queue contains the specified element.
     *
     * @param obj the element to search for
     * @return {@code true} if an equal element is present; otherwise, {@code false}
     */
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
    /**
     * Removes the first element equal to the specified object.
     *
     * @param obj the element to remove
     * @return {@code true} if an element was removed; otherwise, {@code false}
     */
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
    /**
     * Removes the element at the specified zero-based position.
     *
     * @param position the position of the element to remove
     * @return {@code true} if an element was removed; otherwise, {@code false}
     */
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
    /**
     * Creates a list containing the queue's elements in FIFO order.
     *
     * @return a list of the queue's elements
     */
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
    /**
     * Finds the zero-based position of the first element equal to the specified
     * object.
     *
     * @param obj the element to search for
     * @return the element's position, or {@code -1} if it is not present
     */
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
