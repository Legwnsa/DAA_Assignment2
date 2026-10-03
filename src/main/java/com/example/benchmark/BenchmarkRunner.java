package com.example.benchmark;

import com.example.metrics.OpCounter;
import com.example.structures.DynamicArray;
import com.example.structures.MinHeap;
import com.example.structures.MyLinkedList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class BenchmarkRunner {

    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int RUNS = 5;

    public static void main(String[] args) {
        new java.io.File("results").mkdirs();
        String csvFile = "results/results.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                runW1(writer, n);
                runW2(writer, n);
                runW3(writer, n, "head");
                runW3(writer, n, "middle");
                runW4(writer, n);
            }

            System.out.println("Бенчмарк успешно завершен! Результаты сохранены в " + csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runW1(PrintWriter writer, int n) {
        runBenchmarkWithWarmup(writer, "W1_RandomAccess", "-", "DynamicArray", n, (counter) -> {
            DynamicArray arr = new DynamicArray(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) arr.add(rnd.nextInt());
            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 10000; i++) arr.get(rnd.nextInt(n));
            return System.nanoTime() - start;
        });

        runBenchmarkWithWarmup(writer, "W1_RandomAccess", "-", "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) list.add(rnd.nextInt());
            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 10000; i++) list.get(rnd.nextInt(n));
            return System.nanoTime() - start;
        });
    }

    private static void runW2(PrintWriter writer, int n) {
        int[] hits = new int[500];
        int[] misses = new int[500];

        runBenchmarkWithWarmup(writer, "W2_Search", "-", "DynamicArray", n, (counter) -> {
            DynamicArray arr = new DynamicArray(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) {
                int val = rnd.nextInt(1000000);
                arr.add(val);
                if (i < 500) hits[i] = val;
            }
            for (int i = 0; i < 500; i++) misses[i] = -1 - i;

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 500; i++) {
                arr.contains(hits[i]);
                arr.contains(misses[i]);
            }
            return System.nanoTime() - start;
        });

        runBenchmarkWithWarmup(writer, "W2_Search", "-", "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) {
                int val = rnd.nextInt(1000000);
                list.add(val);
                if (i < 500) hits[i] = val;
            }
            for (int i = 0; i < 500; i++) misses[i] = -1 - i;

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 500; i++) {
                list.contains(hits[i]);
                list.contains(misses[i]);
            }
            return System.nanoTime() - start;
        });
    }

    private static void runW3(PrintWriter writer, int n, String variant) {
        int idx = variant.equals("head") ? 0 : n / 2;

        runBenchmarkWithWarmup(writer, "W3_InsertRemove", variant, "DynamicArray", n, (counter) -> {
            DynamicArray arr = new DynamicArray(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) arr.add(rnd.nextInt());

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 1000; i++) arr.add(idx, 999);
            for (int i = 0; i < 1000; i++) arr.remove(idx);
            return System.nanoTime() - start;
        });

        runBenchmarkWithWarmup(writer, "W3_InsertRemove", variant, "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) list.add(rnd.nextInt());

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 1000; i++) list.add(idx, 999);
            for (int i = 0; i < 1000; i++) list.remove(idx);
            return System.nanoTime() - start;
        });
    }

    private static void runW4(PrintWriter writer, int n) {
        runBenchmarkWithWarmup(writer, "W4_PriorityProcessing", "-", "MinHeap", n, (counter) -> {
            MinHeap heap = new MinHeap(counter);
            Random rnd = new Random(42);

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < n; i++) heap.insert(rnd.nextInt());
            for (int i = 0; i < n; i++) heap.extractMin();
            return System.nanoTime() - start;
        });
    }

    @FunctionalInterface
    interface BenchmarkTask {
        long execute(OpCounter counter);
    }

    private static void runBenchmarkWithWarmup(PrintWriter writer, String workload, String variant, String structure, int n, BenchmarkTask task) {
        double[] timesMs = new double[RUNS];
        long[] steps = new long[RUNS];
        long[] moves = new long[RUNS];
        long[] comparisons = new long[RUNS];

        for (int r = 0; r < RUNS; r++) {
            OpCounter counter = new OpCounter();
            long timeNano = task.execute(counter);

            timesMs[r] = timeNano / 1e6;
            steps[r] = counter.getSteps();
            moves[r] = counter.getMoves();
            comparisons[r] = counter.getComparisons();
        }

        Arrays.sort(timesMs);
        Arrays.sort(steps);
        Arrays.sort(moves);
        Arrays.sort(comparisons);

        int medianIdx = RUNS / 2;
        writer.printf("%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload, variant, structure, n,
                timesMs[medianIdx], steps[medianIdx], moves[medianIdx], comparisons[medianIdx]);
    }
}