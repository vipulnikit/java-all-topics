package com.scaler.lld.collections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapNoConcModExcep {
   public static void main(String[] args) throws InterruptedException {
      Map<Integer, Integer> map = new ConcurrentHashMap<>();
      // Collections.synchronizedMap() won't work here because it is just a wrapper over HashMap and its views like
      // entrySet(), keySet(), values() etc aren't synchronized. Only get(), put(), remove() etc are synchronized.
      // Map<Integer, Integer> map = Collections.synchronizedMap(new HashMap<>());
      for (int i = 0; i < 5; i++) {
         map.put(i, i);
      }

      Thread t1 = new Thread(() -> {
         // No ConcurrentModificationException as ConcurrentHashMap is thread-safe.
         for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("size " + map.size());
            System.out.println(entry.getKey() + " " + entry.getValue());
            try {
               // Waiting for t2 to start and modify map. Even after map is modified, t1 will not throw ConcurrentModificationException.
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               System.out.println("Thread interrupted");
            }
         }
      });

      Thread t2 = new Thread(() -> {
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
