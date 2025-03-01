package com.scaler.lld.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
Implement multithreaded merge sort.
*/

class Demo3 {
   public static void main(String[] args) throws Exception {
      List<Integer> l = List.of(7, 3, 1, 2, 4, 6, 17, 12);
      ExecutorService executorService = Executors.newCachedThreadPool();
      MergeSort mergeSort = new MergeSort(l, executorService);
      Future<List<Integer>> output = executorService.submit(mergeSort);
      System.out.println(output.get()); //Blocking Code
      executorService.shutdown();
      executorService.shutdownNow();
   }
}

public class MergeSort implements Callable<List<Integer>> {
   private List<Integer> arr;
   private ExecutorService executor;

   MergeSort(List<Integer> arr, ExecutorService executor) {
      this.arr = arr;
      this.executor = executor;
   }

   @Override
   public List<Integer> call() throws Exception {
      // Business Logic
      // Base case
      if (arr.size() <= 1) {
         return arr;
      }

      // Recursive case
      int n = arr.size();
      int mid = n / 2;
      List<Integer> leftArr = new ArrayList<>();
      List<Integer> rightArr = new ArrayList<>();
      // Division of array into 2 parts
      for (int i = 0; i < mid; i++) {
         leftArr.add(arr.get(i));
      }
      for (int i = mid; i < n; i++) {
         rightArr.add(arr.get(i));
      }

      // Recursively Sort the 2 array
      MergeSort leftMergeSort = new MergeSort(leftArr, executor);
      MergeSort rightMergeSort = new MergeSort(rightArr, executor);
      Future<List<Integer>> leftFuture = executor.submit(leftMergeSort);
      Future<List<Integer>> rightFuture = executor.submit(rightMergeSort);
      leftArr = leftFuture.get();
      rightArr = rightFuture.get();

      // Merge
      return merge(leftArr, rightArr);
   }

   private static List<Integer> merge(List<Integer> leftArr, List<Integer> rightArr) {
      List<Integer> output = new ArrayList<>();
      int i = 0, j = 0;
      while (i < leftArr.size() && j < rightArr.size()) {
         if (leftArr.get(i) <= rightArr.get(j)) {
            output.add(leftArr.get(i++));
         } else {
            output.add(rightArr.get(j++));
         }
      }
      // copy the remaining elements
      while (i < leftArr.size()) {
         output.add(leftArr.get(i++));
      }
      while (j < rightArr.size()) {
         output.add(rightArr.get(j++));
      }
      return output;
   }
}