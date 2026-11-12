package com.paulo.nader.demo;

import java.util.NoSuchElementException;

public class SLQueue<T> {
    private class Node
    {
        private T data;
        private Node next;

        public Node(T data)
        {
            this.data = data;
            next = null;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public SLQueue()
    {
        front = null;
        rear = null;
        size = 0;
    }

        public void enqueue(T element)
    {
        Node newNode = new Node(element);

        if (front == null)
        {
            front = newNode;
            rear = newNode;
        }
        else
        {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    public T dequeue()
    {
        if (front == null)
        {
            throw new NoSuchElementException();
        }

        T removed = front.data;
        front = front.next;
        size--;

        if (front == null)
        {
            rear = null;
        }

        return removed;
    }

    public T peek()
    {
        if (front == null)
        {
            throw new NoSuchElementException();
        }

        return front.data;
    }

    public boolean isEmpty()
    {
        return front == null;
    }

    public int size()
    {
        return size;
    }

}
