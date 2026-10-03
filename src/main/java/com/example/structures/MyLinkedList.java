package com.example.structures;

import com.example.metrics.OpCounter;

public class MyLinkedList {
    private static class Node {
        int val;
        Node next;
        Node prev;

        Node(int val) {
            this.val = val;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final OpCounter counter;

    public MyLinkedList(OpCounter counter) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.counter = counter;
    }

    public void add(int element) {
        Node newNode = new Node(element);
        counter.addMove();

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            counter.addMoves(2);
            tail = newNode;
        }
        size++;
    }

    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == size) {
            add(element);
            return;
        }

        Node newNode = new Node(element);
        counter.addMove();

        if (index == 0) {
            newNode.next = head;
            head.prev = newNode;
            counter.addMoves(2);
            head = newNode;
        } else {
            Node curr = getNode(index);
            Node prevNode = curr.prev;

            newNode.next = curr;
            newNode.prev = prevNode;
            prevNode.next = newNode;
            curr.prev = newNode;
            counter.addMoves(4);
        }
        size++;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return getNode(index).val;
    }

    private Node getNode(int index) {
        Node curr;
        if (index < size / 2) {
            curr = head;
            for (int i = 0; i < index; i++) {
                counter.addStep();
                curr = curr.next;
            }
        } else {
            curr = tail;
            for (int i = size - 1; i > index; i--) {
                counter.addStep();
                curr = curr.prev;
            }
        }
        counter.addStep();
        return curr;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node target = getNode(index);

        if (target.prev != null) {
            target.prev.next = target.next;
            counter.addMove();
        } else {
            head = target.next;
        }

        if (target.next != null) {
            target.next.prev = target.prev;
            counter.addMove();
        } else {
            tail = target.prev;
        }

        size--;
        return target.val;
    }

    public boolean contains(int element) {
        Node curr = head;
        while (curr != null) {
            counter.addStep();
            counter.addComparison();
            if (curr.val == element) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    public int size() {
        return size;
    }
}