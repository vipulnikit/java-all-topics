package com.scaler.lld.multithreading;

import java.util.concurrent.Semaphore;

public class TrafficControlSystem {
   private Semaphore roadASemaphore = new Semaphore(1); // Semaphore for Road A's traffic signal
   private Semaphore roadBSemaphore = new Semaphore(0); // Semaphore for Road B's traffic signal

   // Simulate traffic on Road A
   private void trafficOnRoadA() {
      while (true) {
         try {
            roadASemaphore.acquire();
            System.out.println("Road A: Green");
            Thread.sleep(3000); // Green light duration
            System.out.println("Road A: Yellow");
            Thread.sleep(3000); // Yellow light duration
            System.out.println("Road A: Red");
            roadBSemaphore.release(); // Switch to Road B
         } catch (InterruptedException e) {
            System.out.println("roadA interrupted!");
            Thread.currentThread().interrupt();
         }
      }
   }
   // Simulate traffic on Road B
   private void trafficOnRoadB() {
      while (true) {
         try {
            roadBSemaphore.acquire(); // Acquire the semaphore for Road B
            System.out.println("Road B: Green");
            Thread.sleep(3000); // Green light duration
            System.out.println("Road B: Yellow");
            Thread.sleep(3000); // Yellow light duration
            System.out.println("Road B: Red");
            roadASemaphore.release(); // Switch to Road A
         } catch (InterruptedException e) {
            System.out.println("roadB interrupted!");
            Thread.currentThread().interrupt();
         }
      }
   }
   public static void main(String[] args) {
      TrafficControlSystem control = new TrafficControlSystem();
      // Create and start threads for traffic on Road A and Road B
      Thread roadAThread = new Thread(control::trafficOnRoadA);
      Thread roadBThread = new Thread(control::trafficOnRoadB);
      roadAThread.start();
      roadBThread.start();
   }
}