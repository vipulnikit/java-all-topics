package com.scaler.lld.multithreading;

/*
Write a program to print numbers from 1 to 100 using 100 different threads.
Since you can't control the order of execution of threads, it is okay to get these numbers in any order.
*/

public class NumberPrinter implements Runnable {
   int number;

   NumberPrinter(int number) {
      this.number = number;
   }

   @Override
   public void run() {
      System.out.println("Printing " + number + " from " + Thread.currentThread().getName());
   }
}

class Demo2 {
   public static void main(String[] args) {
      for (int i = 0; i < 100; i++) {
         Thread t = new Thread(new NumberPrinter(i));
         t.start();
      }
   }
}