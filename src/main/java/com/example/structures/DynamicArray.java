package com.example.structures;

import com.example.metrics.OpCounter;

public class DynamicArray {
    private int[] data;
    private int size;
    private final OpCounter counter;

    public DynamicArray(OpCounter counter) {
        this.data = new int[10];
        this.size = 0;
        this.counter = counter;
    }

    public void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length *2;
            int[] newData = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.addStep();
                newData[i] = data[i];
                counter.addMove();
            }
            data = newData;
        }
    }

    public void add(int element) {
        ensureCapacity();;
        data[size] = element;
        counter.addMove();
        size++;
    }

    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity();

        for (int i = size; i > index; i--) {
            counter.addStep();
            data[i] = data[i-1];
            counter.addMove();
        }

        data[index] = element;
        counter.addMove();
        size++;
    }

    public int get(int index) {
        if ( index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.addStep();
        return data[index];
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.addStep();
        int removedValue = data[index];

        for (int i = index; i < size-1; i++) {
            counter.addStep();
            data[i] = data[i+1];
            counter.addMove();
        }

        size--;
        return removedValue;
    }

    public boolean contains(int element) {
        for (int i = 0; i < size; i++) {
            counter.addStep();
            counter.addComparison();
            if (data[i] == element) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }
}
