
package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SLQueueTest {

    private SLQueue<String> queue;

    @BeforeEach
    public void setUp() {
        queue = new SLQueue<String>();
    }

    @Test
    public void testEnqueue() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertEquals(2, queue.size());
        assertEquals("A", queue.peek());
    }

    @Test
    public void testEnqueueNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            queue.enqueue(null);
        });
    }

    @Test
    public void testDequeue() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals("A", queue.dequeue());
        assertEquals(2, queue.size());
        assertEquals("B", queue.peek());
    }

    @Test
    public void testDequeueEmpty() {
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @Test
    public void testPeek() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertEquals("A", queue.peek());
        assertEquals(2, queue.size());
    }

    @Test
    public void testPeekEmpty() {
        assertThrows(NoSuchElementException.class, () -> queue.peek());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(queue.isEmpty());

        queue.enqueue("A");

        assertFalse(queue.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, queue.size());

        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals(3, queue.size());
    }

    @Test
    public void testClear() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    public void testClearEmpty() {
        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    public void testContains() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertTrue(queue.contains("B"));
        assertFalse(queue.contains("C"));
    }

    @Test
    public void testRemoveObject() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertTrue(queue.remove("B"));
        assertFalse(queue.contains("B"));
        assertEquals(2, queue.size());
    }

    @Test
    public void testRemoveObjectNotFound() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertFalse(queue.remove("C"));
        assertEquals(2, queue.size());
    }

    @Test
    public void testRemoveFront() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertTrue(queue.remove("A"));
        assertEquals("B", queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    public void testRemoveRear() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertTrue(queue.remove("B"));

        assertEquals(1, queue.size());
        assertEquals("A", queue.peek());

        queue.enqueue("C");

        assertEquals(Arrays.asList("A", "C"), queue.toList());
    }

    @Test
    public void testRemovePosition() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertTrue(queue.remove(1));

        assertEquals(Arrays.asList("A", "C"), queue.toList());
    }

    @Test
    public void testRemoveFirstPosition() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertTrue(queue.remove(0));
        assertEquals("B", queue.peek());
    }

    @Test
    public void testRemoveInvalidPosition() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertFalse(queue.remove(-1));
        assertFalse(queue.remove(5));

        assertEquals(2, queue.size());
    }

    @Test
    public void testToList() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals(Arrays.asList("A", "B", "C"), queue.toList());
    }

    @Test
    public void testToListEmpty() {
        assertTrue(queue.toList().isEmpty());
    }

    @Test
    public void testGetPosition() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals(0, queue.getPosition("A"));
        assertEquals(1, queue.getPosition("B"));
        assertEquals(2, queue.getPosition("C"));
    }

    @Test
    public void testGetPositionNotFound() {
        queue.enqueue("A");
        queue.enqueue("B");

        assertEquals(-1, queue.getPosition("C"));
    }
}