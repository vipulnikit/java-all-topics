package com.scaler.lld.collections;

import java.util.HashMap;

public class HashMapNotSynchronized {
   public static void main(String[] args) throws InterruptedException {
      HashMap<Integer, Integer> map = new HashMap<>();

      Thread t1 = new Thread(() -> {
         for (int i = 0; i < 1000; i++) {
            map.put(i, i);
         }
      });

      Thread t2 = new Thread(() -> {
         for (int i = 1000; i < 2000; i++) {
            map.put(i, i);
         }
      });

      t1.start();
      t2.start();

      t1.join();
      t2.join();

      // Race Condition: size should be 2000 but might be less. Notice that it can't be > 2000. see reason below for size < 2000.
      // Also, we won't get ConcurrentModificationException because it occurs when map is being read and inserted simultaneously.
      System.out.println("Map Size: " + map.size());
   }
}

/*
HashMap is not synchronized. Means put(), get(), remove() operations are not thread-safe.

Reason 1: Two Threads Writing to the Same Bucket Index (Hash Collision)
t1 finds bucket index i is empty and inserts the key1,value1 pair in that index.
t2 also finds same bucket index i as empty and inserts the key2,value2 pair in that index overwriting the pair inserted by t1.
This leads to missed writes.

Reason 2: Rehashing (Resizing Issue)
t1 & t2 both detect that the HashMap needs resizing at the same time.
Both threads start rehashing simultaneously but without synchronization.
t1 completes rehashing and copies the old entries to the new table. Then inserts new key1,value1 pair in the new table.
t2 completes rehashing later and then inserts new key2,value2 pair in the new table created by t2.
Due to this, entry key1,value1 stored in new table created by t1 is lost. Final new table is the one created by t2.
This leads to missed writes.
*/