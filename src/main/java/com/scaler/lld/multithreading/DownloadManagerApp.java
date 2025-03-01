package com.scaler.lld.multithreading;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
Consider a simple download manager application that needs to download multiple files concurrently.
Implement the download manager using the Java Executor Framework.
Requirements:
1. The download manager should be able to download multiple files simultaneously.
2. Each file download is an independent task that can be executed concurrently.
3. The download manager should use a thread pool from the Executor Framework to manage and execute the download tasks.
4. Implement a mechanism to track the progress of each download task and display it to the user.
*/

class DownloadTask implements Callable<String> {
   private final String fileUrl;

   public DownloadTask(String fileUrl) {
      this.fileUrl = fileUrl;
   }

   @Override
   public String call() {
      // Simulate download progress
      for (int progress = 0; progress <= 100; progress += 10) {
         System.out.println("Progress for " + fileUrl + ": " + progress + "%");
         try {
            Thread.sleep(500); // Simulate download time
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
      return fileUrl;
   }
}

class DownloadManager {
   // <String> represents the fileUrl returned after download
   private final ExecutorService executorService;
   private final ExecutorCompletionService<String> completionService;
   private final AtomicInteger totalFiles;
   private final AtomicInteger filesDownloaded;

   public DownloadManager(int threadPoolSize) {
      executorService = Executors.newFixedThreadPool(threadPoolSize);
      completionService = new ExecutorCompletionService<>(executorService);
      totalFiles = new AtomicInteger(0);
      filesDownloaded = new AtomicInteger(0);
   }

   public void downloadFiles(List<String> fileUrls) {
      // Submit download tasks to the executor service
      totalFiles.addAndGet(fileUrls.size());
      for (String fileUrl : fileUrls) {
         DownloadTask downloadTask = new DownloadTask(fileUrl);
         completionService.submit(downloadTask);
      }
      trackDownloadingFiles(fileUrls.size());
   }

   public void trackDownloadingFiles(int filesCount) {
      // New thread to track and update the downloaded files count. This thread doesn't need completionService.
      executorService.submit(() -> {
         for (int i = 0; i < filesCount; i++) {
            try {
               // take() waits and returns next completed task's Future whichever completes first. So, we can use the file as soon as it's downloaded.
               // This is the advantage of using ExecutorCompletionService over ExecutorService.
               String fileUrl = completionService.take().get();
               System.out.println("File Downloaded: " + fileUrl);
               filesDownloaded.incrementAndGet();
            } catch (CancellationException | InterruptedException | ExecutionException e) {
               System.out.println("Error getting update for downloading files.");
            }
         }
      });
   }

   public int getTotalFiles() {
      return totalFiles.get();
   }

   public int getDownloadedFiles() {
      return filesDownloaded.get();
   }

   public void shutdown() {
      executorService.shutdown();
      executorService.shutdownNow();
   }
}

public class DownloadManagerApp {
   public static void main(String[] args) {
      DownloadManager downloadManager = new DownloadManager(3);

      List<String> files1 = List.of("file1", "file2", "file3", "file4", "file5");
      downloadManager.downloadFiles(files1);

      while (downloadManager.getDownloadedFiles() < downloadManager.getTotalFiles()) {
         System.out.println("Downloaded " + downloadManager.getDownloadedFiles() + " out of " + downloadManager.getTotalFiles() + " files.");
         try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
      System.out.println("Downloaded " + downloadManager.getDownloadedFiles() + " out of " + downloadManager.getTotalFiles() + " files.");
      downloadManager.shutdown();
   }
}