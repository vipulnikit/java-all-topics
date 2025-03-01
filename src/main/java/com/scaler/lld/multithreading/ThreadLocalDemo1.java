package com.scaler.lld.multithreading;

public class ThreadLocalDemo1 {
   public static void main(String[] args) throws InterruptedException {
      System.out.println("Without ThreadLocal");
      Resource1 resource1 = new Resource1();
      Thread t1 = new Thread(new WithoutThreadLocal1(resource1), "user1");
      Thread t2 = new Thread(new WithoutThreadLocal1(resource1), "user2");
      t1.start();
      // Waiting for t1 to decrement resource.data so that it can be read by t2.
      Thread.sleep(1000);
      t2.start();
      t1.join();
      t2.join();

      System.out.println("With ThreadLocal");
      Resource2 resource2 = new Resource2();
      Thread t3 = new Thread(new WithThreadLocal1(resource2), "user1");
      Thread t4 = new Thread(new WithThreadLocal1(resource2), "user2");
      t3.start();
      // Waiting for t1 to decrement resource.data so that it can be read by t2.
      Thread.sleep(1000);
      t4.start();
      t3.join();
      t4.join();
   }
}

class WithoutThreadLocal1 implements Runnable {
   private Resource1 resource1;

   WithoutThreadLocal1(Resource1 resource1) {
      this.resource1 = resource1;
   }

   public void run() {
      System.out.println(Thread.currentThread().getName() + " before " + resource1.data);
      // This change will be shared among both threads t1, t2.
      resource1.data--;
      try {
         // Simulate some processing time so that other thread can read the decremented value.
         Thread.sleep(3000);
      } catch (InterruptedException e) {
         System.out.println("WithoutThreadLocal Interrupted" + e.getMessage());
      }
      System.out.println(Thread.currentThread().getName() + " after " + resource1.data);
   }
}

// Notice that we are making Resource2.data ThreadLocal not resource2.
class WithThreadLocal1 implements Runnable {
   private Resource2 resource2;

   WithThreadLocal1(Resource2 resource2) {
      this.resource2 = resource2;
   }

   public void run() {
      System.out.println(Thread.currentThread().getName() + " before " + resource2.data.get());
      // This change will be set per thread.
      resource2.data.set(resource2.data.get() - 1);
      try {
         // Simulate some processing time so that other thread can decrement resource2.data parallelly.
         // Since resource2.data is set per thread, change will not be shared among threads.
         Thread.sleep(3000);
      } catch (InterruptedException e) {
         System.out.println("WithThreadLocal Interrupted" + e.getMessage());
      }
      System.out.println(Thread.currentThread().getName() + " after " + resource2.data.get());
   }
}

class Resource1 {
   int data = 10;
}

class Resource2 {
   ThreadLocal<Integer> data = ThreadLocal.withInitial(() -> 10);
}