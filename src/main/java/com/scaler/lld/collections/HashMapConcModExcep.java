package com.scaler.lld.collections;

import java.util.HashMap;
import java.util.Map;

public class HashMapConcModExcep {
   public static void main(String[] args) throws InterruptedException {
      HashMap<Integer, Integer> map = new HashMap<>();
      for (int i = 0; i < 5; i++) {
         map.put(i, i);
      }

      Thread t1 = new Thread(() -> {
         // Will throw ConcurrentModificationException. Reason: t2 has modified(insert) map while t1 is iterating over it.
         for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
            try {
               // Waiting for t2 to start and modify map. After map is modified, t1 will throw ConcurrentModificationException.
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               System.out.println("Thread interrupted");
            }
         }
      });

      Thread t2 = new Thread(() -> {
         // Notice: we are modifying(put()/remove()) map size. Updating existing keys won't throw ConcurrentModificationException.
         map.put(5, 5);
      });

      t1.start();
      // Allowing t1 to start first.
      Thread.sleep(1000);
      t2.start();

      t1.join();
      t2.join();
   }
}
