Assignment 2: Data Structures & In-Memory Workload Engine

Student: Temirlan Zhangirkhan  
Group: SE-2521  
Course: Data Structures & Algorithms (DAA)  

------------------------------------------------------
Overview
This project implements custom in-memory data structures from scratch in Java without using `java.util` collection abstractions (such as `ArrayList`, `LinkedList`, or `PriorityQueue`). It includes an empirical benchmark suite measuring execution time, step counts, object allocations, and comparisons across four distinct synthetic workloads.

------------------------------------------------------
Implemented Data Structures
1. `DynamicArray`: Resizable array implementation featuring amortized O(1) appends, element shifts on insert/remove, and O(1) random access.
2. `MyLinkedList`: Singly linked list with head and tail pointers, demonstrating O(1) boundary operations and O(N) index-based access.
3. `MinHeap`: Binary min-heap backed by a dynamic array, supporting O(log N) insertions/extractions and O(1) minimum peek.

------------------------------------------------------
Workloads Implemented
- `W1_RandomAccess`: Evaluates O(1) indexed retrieval (`get(i)`) vs O(N) node traversal.
- `W2_Search`: Evaluates linear search (`contains(x)`) performance, contrasting contiguous memory with pointer chasing.
- `W3_InsertRemove`: Compares boundary (head) insertions/deletions against middle-index operations.
- `W4_PriorityProcessing`: Simulates event queue processing using `MinHeap` (`insert` + `extractMin`).
------------------------------------------------------
Performance Benchmarks & Plots
All benchmarks were evaluated for sizes N in {100, 1000, 10000, 100000} using median values over multiple warm-up and measured runs.

| Workload | Executable Plot | Primary Finding |
|
| W1: Random Access | `results/plots/W1_RandomAccess.png` | `DynamicArray` takes <1text{ ms} due to direct pointer math, whereas `MyLinkedList` scales linearly up to 300+text{ ms}. |
| W2: Linear Search | `results/plots/W2_Search.png` | `DynamicArray` outperforms `MyLinkedList` by 3times due to CPU L1/L2 cache locality and absence of pointer chasing. |
| W3: Insert/Remove | `results/plots/W3_InsertRemove.png` | `MyLinkedList` at head runs in O(1) time (0text{ ms}), but middle operations scale up to 120text{ ms}. |
| W4: Priority Queue | `results/plots/W4_PriorityProcessing.png` | `MinHeap` handles 100,000 priority operations smoothly in sim10text{ ms} thanks to O(log N) tree depth. |
------------------------------------------------------
Repository Structure
-results/
    plots/
        "4 png files"
    results.csv

-src/main/
    java/com/example
        benchmark/BenchmarkRunner.java
        metrics/OpCounter.java
        structures/
            DynamicArray.java
            MinHeap.java
            MyLinkedList.java
    test/java/
        DynamicArrayTest
        MinHeapTest
        MyLinkedListTest
    
-Assignment 2 Report TEmirlan.pdf
-README.md

How to Run
Requirements:
-JDK 17+
-Git

Build and Run Benchmarks:
git clone https://github.com/Legwnsa/DAA_Assignment2.git
cd DAA_Assignment2

javac -d bin src//*.java

java -cp bin workloads.Main

Final Report
For formal asymptotic proofs, loop invariant analysis (contains), and hardware cache locality discussion, refer to REPORT.pdf.