package com.scaler.lld.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalThreadPool {
   ThreadLocal<String> message = ThreadLocal.withInitial(() -> "default");

   void threadLocalProblemWithThreadPool() throws InterruptedException {
      ExecutorService executorService = Executors.newFixedThreadPool(2);
      for (int i = 0; i < 3; i++) {
         final String thread = "thread-" + i;
         final String taskMsg = "msg-" + i;
         executorService.submit(() -> {
            // i=0,1 will print "default" but i=2 will reuse thread created for either i=0 or 1 and print already set value.
            System.out.println(thread + " before " + message.get());
            message.set(taskMsg);
            System.out.println(thread + " after " + message.get());

            // No cleanup here! This will cause inconsistent data.
         });

      }

      executorService.shutdown();
      executorService.awaitTermination(5, TimeUnit.SECONDS);
      executorService.close();
   }

   void threadLocalSolutionWithThreadPool() throws InterruptedException {
      ExecutorService executorService = Executors.newFixedThreadPool(2);
      for (int i = 0; i < 3; i++) {
         final String thread = "thread-" + i;
         final String taskMsg = "msg-" + i;
         executorService.submit(() -> {
            // i=0,1 will print "default" but i=2 will reuse thread created for either i=0 or 1 and print already set value.
            System.out.println(thread + " before " + message.get());
            message.set(taskMsg);
            System.out.println(thread + " after " + message.get());

            message.remove();
         });

      }

      executorService.shutdown();
      executorService.awaitTermination(5, TimeUnit.SECONDS);
      executorService.close();
   }

   public static void main(String[] args) throws InterruptedException {
      ThreadLocalThreadPool threadLocalThreadPool = new ThreadLocalThreadPool();
      System.out.println("Without ThreadLocal Cleanup");
      threadLocalThreadPool.threadLocalProblemWithThreadPool();
      System.out.println("With ThreadLocal Cleanup");
      threadLocalThreadPool.threadLocalSolutionWithThreadPool();
   }
}