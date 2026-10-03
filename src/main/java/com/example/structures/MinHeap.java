package com.example.structures;

import com.example.metrics.OpCounter;

public class MinHeap {
    private int[] heap;
    private int size;
    private final OpCounter counter;

    public MinHeap(OpCounter counter) {
        this.heap = new int[10];
        this.size = 0;
        this.counter = counter;
    }

    public MinHeap(int capacity, OpCounter counter) {
        this.heap = new int[capacity];
        this.size = 0;
        this.counter = counter;
    }

    private void ensureCapacity() {
        if (size == heap.length) {
            int[] newHeap = new int[heap.length * 2];
            for (int i = 0; i < size; i++) {
                counter.addStep();
                newHeap[i] = heap[i];
                counter.addMove();
            }
            heap = newHeap;
        }
    }

    public void insert(int val) {
        ensureCapacity();
        heap[size] = val;
        counter.addMove();
        size++;
        bubbleUp(size - 1);
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.addStep();
        return heap[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.addStep();
        int min = heap[0];

        heap[0] = heap[size - 1];
        counter.addMove();
        size--;

        if (size > 0) {
            bubbleDown(0);
        }

        return min;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            counter.addStep();
            counter.addStep();
            counter.addComparison();

            if (heap[index] < heap[parentIndex]) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void bubbleDown(int index) {
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size) {
                counter.addStep();
                counter.addStep();
                counter.addComparison();
                if (heap[left] < heap[smallest]) {
                    smallest = left;
                }
            }

            if (right < size) {
                counter.addStep();
                counter.addStep();
                counter.addComparison();
                if (heap[right] < heap[smallest]) {
                    smallest = right;
                }
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        int temp = heap[i];
        counter.addStep();
        heap[i] = heap[j];
        counter.addMove();
        heap[j] = temp;
        counter.addMove();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}