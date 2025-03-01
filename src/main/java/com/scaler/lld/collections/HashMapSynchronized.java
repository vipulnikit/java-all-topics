package com.scaler.lld.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapSynchronized {
   public static void main(String[] args) throws InterruptedException {
      HashMap<Integer, Integer> map = new HashMap<>();
      // use Collections.synchronizedMap() or ConcurrentHashMap for synchronized map. Generally prefer ConcurrentHashMap. See HashMapNoConcModExcep.java.
      // synchronizedMap() is a wrapper over HashMap and is synchronized. Modifications in syncMap will also reflect map.
      Map<Integer, Integer> syncMap = Collections.synchronizedMap(map);
//      Map<Integer, Integer> syncMap = new ConcurrentHashMap<>();


      Thread t1 = new Thread(() -> {
         for (int i = 0; i < 1000; i++) {
            syncMap.put(i, i);
         }
      });

      Thread t2 = new Thread(() -> {
         for (int i = 1000; i < 2000; i++) {
            syncMap.put(i, i);
         }
      });

      t1.start();
      t2.start();

      t1.join();
      t2.join();

      // Notice: we are using map.size() instead of syncMap.size() when using synchronizedMap().
      System.out.println("Map Size: " + map.size());
      // If using ConcurrentHashMap use syncMap.size().
//      System.out.println("Map Size: " + syncMap.size());

   }
}