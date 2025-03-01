package com.scaler.lld.multithreading;

public class InterThreadComm {
   public static void main(String[] args) throws InterruptedException {
      Queue queue = new Queue(10);
      Thread producer = new Thread(new Producer(queue));
      Thread consumer = new Thread(new Consumer(queue));

      producer.start();
      consumer.start();

      producer.join();
      consumer.join();
   }
}

class Queue {
   int size, num;
   boolean isProduced;

   Queue(int size) {
      isProduced = false;
      this.size = size;
   }

   synchronized void put(int num) {
      while (isProduced) {
         try {
            wait();
         } catch (InterruptedException e) {
            System.out.println("Producer waiting for Consumer to consume interrupted: " + e.getMessage());
         }
      }
      System.out.println("Produced: " + num);
      this.num = num;
      isProduced = true;
      // Notice that notify() doesn't release the lock on this Queue object. Lock is released only when thread exits this synchronized method.
      // It only awakens one waiting thread out of all waiting threads. Which thread gets awakened out of all waiting is un-deterministic and depends on OS.
      notify();
   }

   synchronized int get() {
      while (!isProduced) {
         try {
            wait();
         } catch (InterruptedException e) {
            System.out.println("Consumer waiting for Producer to produce interrupted: " + e.getMessage());
         }
      }
      System.out.println("Consumed: " + num);
      isProduced = false;
      // Notice that notify() doesn't release the lock on this Queue object. Lock is released only when thread exits this synchronized method.
      // It only awakens one waiting thread out of all waiting threads. Which thread gets awakened out of all waiting is un-deterministic and depends on OS.
      notify();
      return num;
   }
}

class Producer implements Runnable {
   Queue queue;

   Producer(Queue queue) {
      this.queue = queue;
   }

   public void run() {
      for (int i = 0; i < queue.size; i++) {
         queue.put(i);
      }
   }
}

class Consumer implements Runnable {
   Queue queue;

   Consumer(Queue queue) {
      this.queue = queue;
   }

   public void run() {
      for (int i = 0; i < queue.size; i++) {
         queue.get();
      }
   }
}