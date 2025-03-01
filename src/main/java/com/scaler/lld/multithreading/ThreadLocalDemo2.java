package com.scaler.lld.multithreading;

public class ThreadLocalDemo2 {
   UserSession userSession1 = new UserSession("default");
   ThreadLocal<UserSession> userSession2 = ThreadLocal.withInitial(() -> new UserSession("default"));

   public static void main(String[] args) throws InterruptedException {
      ThreadLocalDemo2 threadLocalDemo2 = new ThreadLocalDemo2();
//      System.out.println("Without ThreadLocal");
//      threadLocalDemo2.withoutThreadLocal();
      System.out.println("With ThreadLocal");
      threadLocalDemo2.withThreadLocal();
   }

   void withoutThreadLocal() throws InterruptedException {
      Runnable task = () -> {
         System.out.println(Thread.currentThread().getName() + " before " + userSession1.userName);
         userSession1 = new UserSession(Thread.currentThread().getName());
         try {
            // Simulate some processing time so that other thread can read the decremented value.
            Thread.sleep(3000);
         } catch (InterruptedException e) {
            System.out.println("WithoutThreadLocal Interrupted" + e.getMessage());
         }
         // both threads will read the same value of userSession1
         System.out.println(Thread.currentThread().getName() + " after " + userSession1.userName);
      };
      Thread t1 = new Thread(task,"user1");
      Thread t2 = new Thread(task,"user2");
      t1.start();
      Thread.sleep(1000);
      t2.start();
      t1.join();
      t2.join();
   }

   // Notice that we are making userSession2 ThreadLocal not UserSession.userName.
   void withThreadLocal() throws InterruptedException {
      Runnable task = () -> {
         System.out.println(Thread.currentThread().getName() + " before " + userSession2.get().userName);
         // This change will be set per thread.
         userSession2.set(new UserSession(Thread.currentThread().getName()));
         try {
            // Simulate some processing time so that other thread can modify userSession2 parallelly.
            // Since userSession2 is set per thread, change will not be shared among threads.
            Thread.sleep(3000);
         } catch (InterruptedException e) {
            System.out.println("WithThreadLocal Interrupted" + e.getMessage());
         }
         // both threads will read its own value of userSession2
         System.out.println(Thread.currentThread().getName() + " after " + userSession2.get().userName);
      };
      Thread t1 = new Thread(task,"user1");
      Thread t2 = new Thread(task,"user2");
      t1.start();
      Thread.sleep(1000);
      t2.start();
      t1.join();
      t2.join();
   }
}

class UserSession {
   public String userName;

   public UserSession(String userName) {
      this.userName = userName;
   }
}