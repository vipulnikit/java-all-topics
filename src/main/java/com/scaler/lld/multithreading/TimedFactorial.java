package com.scaler.lld.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/*
Write a program that computes Factorial of a list of numbers. Each factorial should be computed on a separate thread.
For each factorial calculation, do not wait for more than 2 seconds.
*/

public class TimedFactorial implements Callable<Integer> {
    int n;

    public TimedFactorial(int n) {
        this.n = n;
    }

    @Override
    public Integer call() throws InterruptedException {
        if (n % 2 == 0) {
            Thread.sleep(10000);    // wait 10 sec so that we don't get result in 2 secs for this n.
        }
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}

// Version 2 - Using ScheduledExecutorService
class CancelTask<V> {
    private final Future<V> future;

    CancelTask(Future<V> future) {
        this.future = future;
    }

    public boolean cancel() {
        // if future already completed, then cancel() has no effect and returns false.
        return future.cancel(true);
    }
}

class Demo1 {
    public static void main(String[] args) {
        final int WAIT_MS = 2000;
        List<Integer> nums = List.of(2, 3, 4, 5);

        ExecutorService startPool = Executors.newCachedThreadPool();
        ScheduledExecutorService cancelPool = Executors.newScheduledThreadPool(nums.size());
        List<Future<Integer>> factFutures = new ArrayList<>();
        List<Future<Boolean>> cancelFutures = new ArrayList<>();

        for (int num : nums) {
            TimedFactorial factorial = new TimedFactorial(num);
            Future<Integer> factFuture = startPool.submit(factorial);    // factorial is Callable instance.

            CancelTask<Integer> cancelTask = new CancelTask<>(factFuture);
            // Ideally, cancelTask should start immediately after 2 sec from current time but might not happen. See below comments.
            Future<Boolean> cancelFuture = cancelPool.schedule(cancelTask::cancel, WAIT_MS, TimeUnit.MILLISECONDS);  // here cancel() behaves as call() method of Callable.

            cancelFutures.add(cancelFuture);
            factFutures.add(factFuture);
        }
        // Waiting for 2 sec for all factorials to be calculated or cancelled by the CancelTask for factorials taking longer than 2 sec.
        try {
            Thread.sleep(WAIT_MS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for all factorials to be calculated!");
        }
        for (int i = 0; i < nums.size(); i++) {
            try {
                // In rare scenarios, cancel task might start later than 2 sec due to Thread Pool Manager not assigning cancel task to a thread in thead pool.
                // In this case, for longer threads, we will end up waiting for factFutures.get(i).get() and in between cancel task will trigger
                // and cancel the current factFutures.get(i) which will result in CancellationException for current factFutures.get(i).
                // So waiting for cancel task to return before checking its factorial.
                // get() is blocking action. It should return immediately in most scenarios as this thread is already sleeping for 2 secs above.
                cancelFutures.get(i).get();
                if (!factFutures.get(i).isCancelled()) {
                    System.out.println("Factorial of " + nums.get(i) + ": " + factFutures.get(i).get());    // get() is blocking action.
                } else {
                    System.out.println("Factorial of " + nums.get(i) + " timed out!");
                }
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                System.out.println("Error getting result from future for num: " + nums.get(i));
            }
        }

        startPool.shutdown();
        startPool.shutdownNow();
        cancelPool.shutdown();
        cancelPool.shutdownNow();
        startPool.close();
        cancelPool.close();
    }
}

//// Version 1 - Using invokeAll(tasks, secs_val, time_unit) of ExecutorService
//class Demo1 {
//    public static void main(String[] args) {
//        final int WAIT_MS = 2000;
//        List<Integer> nums = List.of(2, 3, 4, 5);
//
//        List<TimedFactorial> tasks = new ArrayList<>();
//        for (Integer num : nums) {
//            tasks.add(new TimedFactorial(num));
//        }
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        try {
//            // invokeAll() will terminate all un-completed tasks after the timeout. isCancelled() of those tasks will be true.
//            List<Future<Integer>> futures = executorService.invokeAll(tasks, WAIT_MS, TimeUnit.MILLISECONDS);
//            for (int i = 0; i < nums.size(); i++) {
//                try {
//                    // isDone() is true for all the tasks irrespective of completion or timeout. So using isCancelled().
//                    if (!futures.get(i).isCancelled()) {
//                        System.out.println("Factorial of " + nums.get(i) + ": " + futures.get(i).get());
//                    } else {
//                        System.out.println("Factorial of " + nums.get(i) + " timed out!");
//                    }
//                } catch (InterruptedException e) {
//                    System.out.println("Future waiting for result interrupted!");
//                } catch (ExecutionException e) {
//                    System.out.println("Error getting result from future for num: " + nums.get(i));
//                }
//            }
//        } catch (InterruptedException e) {
//            System.out.println("ExecutorService waiting for tasks to complete for 2 sec was interrupted!");
//        } finally {
//            // executorService.shutdown();
//            // The shutdown() method doesn’t cause immediate destruction of the ExecutorService.
//            // It will make the ExecutorService stop accepting new tasks and shut down after all running threads finish their current work.
//
//            // The shutdownNow() method tries to destroy the ExecutorService immediately, but it doesn’t guarantee
//            // that all the running threads will be stopped at the same time.
//            executorService.shutdownNow();
//            executorService.close();
//
//            // If you want to wait for certain period for the ExecutorService to finish all running tasks before shutdown, use the awaitTermination() method.
//            // Oracle recommends to use both shutdown() & shutdownNow() combined with the awaitTermination() method for this use case.
//            // executorService.shutdown();
//            // try {
//            //     if (!executorService.awaitTermination(1000, TimeUnit.SECONDS)) {
//            //          System.out.println("ExecutorService didn't terminate in 1000 seconds!");
//            //          executorService.shutdownNow();
//            //     }
//            // } catch (InterruptedException e) {
//            //     System.out.println("ExecutorService waiting for termination was interrupted!");
//            //     executorService.shutdownNow();
//            // }
//        }
//    }
//}