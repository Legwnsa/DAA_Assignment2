package com.example.benchmark;

import com.example.metrics.OpCounter;
import com.example.structures.DynamicArray;
import com.example.structures.MinHeap;
import com.example.structures.MyLinkedList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class BenchmarkRunner {

    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int RUNS = 5;
    private static final int WARMUP = 3;
    private static volatile long sink;

    public static void main(String[] args) {
        new java.io.File("results").mkdirs();
        String csvFile = "results/results.csv";

        PrintWriter dummy = new PrintWriter(Writer.nullWriter());
        for (int n : new int[]{1000, 10000}) {
            runAll(dummy, n);
        }
        dummy.close();

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (int n : SIZES) {
                runAll(writer, n);
            }
            System.out.println("Бенчмарк успешно завершен! Результаты сохранены в " + csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runAll(PrintWriter writer, int n) {
        runW1(writer, n);
        runW2(writer, n);
        runW3(writer, n, "head");
        runW3(writer, n, "middle");
        runW4(writer, n);
    }

    private static void runW1(PrintWriter writer, int n) {
        runBenchmarkWithWarmup(writer, "W1_RandomAccess", "-", "DynamicArray", n, (counter) -> {
            DynamicArray arr = new DynamicArray(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) arr.add(rnd.nextInt());
            int[] idx = new int[10000];
            for (int i = 0; i < idx.length; i++) idx[i] = rnd.nextInt(n);

            counter.reset();
            long start = System.nanoTime();
            long sum = 0;
            for (int i : idx) sum += arr.get(i);
            long time = System.nanoTime() - start;
            sink = sum;
            return time;
        });

        runBenchmarkWithWarmup(writer, "W1_RandomAccess", "-", "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) list.add(rnd.nextInt());
            int[] idx = new int[10000];
            for (int i = 0; i < idx.length; i++) idx[i] = rnd.nextInt(n);

            counter.reset();
            long start = System.nanoTime();
            long sum = 0;
            for (int i : idx) sum += list.get(i);
            long time = System.nanoTime() - start;
            sink = sum;
            return time;
        });
    }

    private static void runW2(PrintWriter writer, int n) {
        runBenchmarkWithWarmup(writer, "W2_Search", "-", "DynamicArray", n, (counter) -> {
            DynamicArray arr = new DynamicArray(counter);
            Random rnd = new Random(42);
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = rnd.nextInt(1000000);
                arr.add(values[i]);
            }
            int[] queries = new int[1000];
            for (int i = 0; i < queries.length; i += 2) {
                queries[i] = values[rnd.nextInt(n)];
                queries[i + 1] = -1 - i;
            }

            counter.reset();
            long start = System.nanoTime();
            int found = 0;
            for (int q : queries) {
                if (arr.contains(q)) found++;
            }
            long time = System.nanoTime() - start;
            sink = found;
            return time;
        });

        runBenchmarkWithWarmup(writer, "W2_Search", "-", "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = rnd.nextInt(1000000);
                list.add(values[i]);
            }
            int[] queries = new int[1000];
            for (int i = 0; i < queries.length; i += 2) {
                queries[i] = values[rnd.nextInt(n)];
                queries[i + 1] = -1 - i;
            }

            counter.reset();
            long start = System.nanoTime();
            int found = 0;
            for (int q : queries) {
                if (list.contains(q)) found++;
            }
            long time = System.nanoTime() - start;
            sink = found;
            return time;
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
            long sum = 0;
            for (int i = 0; i < 1000; i++) sum += arr.remove(idx);
            long time = System.nanoTime() - start;
            sink = sum;
            return time;
        });

        runBenchmarkWithWarmup(writer, "W3_InsertRemove", variant, "MyLinkedList", n, (counter) -> {
            MyLinkedList list = new MyLinkedList(counter);
            Random rnd = new Random(42);
            for (int i = 0; i < n; i++) list.add(rnd.nextInt());

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < 1000; i++) list.add(idx, 999);
            long sum = 0;
            for (int i = 0; i < 1000; i++) sum += list.remove(idx);
            long time = System.nanoTime() - start;
            sink = sum;
            return time;
        });
    }

    private static void runW4(PrintWriter writer, int n) {
        runBenchmarkWithWarmup(writer, "W4_PriorityProcessing", "-", "MinHeap", n, (counter) -> {
            MinHeap heap = new MinHeap(counter);
            Random rnd = new Random(42);
            int[] values = new int[n];
            for (int i = 0; i < n; i++) values[i] = rnd.nextInt();

            counter.reset();
            long start = System.nanoTime();
            for (int i = 0; i < n; i++) heap.insert(values[i]);
            int prev = Integer.MIN_VALUE;
            long sum = 0;
            for (int i = 0; i < n; i++) {
                int m = heap.extractMin();
                if (m < prev) throw new IllegalStateException("Heap order broken");
                prev = m;
                sum += m;
            }
            long time = System.nanoTime() - start;
            sink = sum;
            return time;
        });
    }

    @FunctionalInterface
    interface BenchmarkTask {
        long execute(OpCounter counter);
    }

    private static void runBenchmarkWithWarmup(PrintWriter writer, String workload, String variant,
                                               String structure, int n, BenchmarkTask task) {
        for (int w = 0; w < WARMUP; w++) {
            task.execute(new OpCounter());
        }

        double[] timesMs = new double[RUNS];
        long steps = 0;
        long moves = 0;
        long comparisons = 0;

        for (int r = 0; r < RUNS; r++) {
            OpCounter counter = new OpCounter();
            long timeNano = task.execute(counter);
            timesMs[r] = timeNano / 1e6;
            steps = counter.getSteps();
            moves = counter.getMoves();
            comparisons = counter.getComparisons();
        }

        Arrays.sort(timesMs);

        writer.printf(Locale.ROOT, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload, variant, structure, n,
                timesMs[RUNS / 2], steps, moves, comparisons);
    }
}