package com.scaler.lld.multithreading;


import java.util.concurrent.atomic.AtomicInteger;

class Counter {
   // WRONG
//   private int count = 0;
//   public void increment() {
//      count++; // Not atomic, leads to race condition
//   }

   // CORRECT - Option 1
//   private int count = 0;
//   public synchronized void increment() {
//      count++;
//   }

   // CORRECT - Option 2
   private AtomicInteger count = new AtomicInteger(0);
   public void increment() {
      count.incrementAndGet();
   }

   public int getCount() {
      return count.get();
   }
}

public class RaceCondition {
   public static void main(String[] args) throws InterruptedException {
      Counter counter = new Counter();
      Thread t1 = new Thread(() -> {
         for (int i = 0; i < 1000; i++) {
            counter.increment();
         }
      });
      Thread t2 = new Thread(() -> {
         for (int i = 0; i < 1000; i++) {
            counter.increment();
         }
      });

      t1.start(); t2.start();
      t1.join(); t2.join();

      System.out.println("Final Counter Value: " + counter.getCount());
   }
}
