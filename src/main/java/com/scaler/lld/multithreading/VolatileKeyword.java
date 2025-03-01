package com.scaler.lld.multithreading;

public class VolatileKeyword {
   public static void main(String[] args) {
      SharedResource sharedResource = new SharedResource();
      System.out.println("Shared Resource Created, Flag Value " + sharedResource.getFlag());
      Thread A = new Thread(() -> {
         // Without volatile keyword:
         // After 2 secs, toggle the value so that within 2 secs Thread B can read and store the value in its Thread Cache.
         // After 2 secs, even if Thread A will toggle the value, Thread B will still read the old value from its Thread Cache.
         try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            System.out.println("Thread A Interrupted" + e.getMessage());
         }
         sharedResource.toggleFlag();
         System.out.println("Thread A is finished, Flag is " + sharedResource.getFlag());
      });
      Thread B = new Thread(() -> {
         while (!sharedResource.getFlag()) {
            //...busy-wait...
//             System.out.println("Inside Loop " + sharedResource.getFlag());
         }
         System.out.println("In Thread B, Flag is " + sharedResource.getFlag());
      });
      A.start();
      B.start();
   }
}

class SharedResource {
//   private volatile boolean flag;
   private boolean flag;

   SharedResource() {
      this.flag = false;
   }

   public void toggleFlag() {
      this.flag = !this.flag;
   }

   public boolean getFlag() {
      return flag;
   }
}
