package com.scaler.lld.multithreading;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class LibraryBookAllocation {
   private static final int MAX_BOOKS = 3;
   private static final int MAX_STUDENTS = 3;

   public static void main(String[] args) throws InterruptedException {
      Semaphore[] books = new Semaphore[MAX_BOOKS];
      for (int i = 0; i < MAX_BOOKS; i++) {
         books[i] = new Semaphore(1);
      }

      Library library = new Library(books);

      Thread[] students = new Thread[MAX_STUDENTS];
      for (int i = 0; i < MAX_STUDENTS; i++) {
         String studentId = String.valueOf(i);
         students[i] = new Thread(new Student(library), studentId);
         students[i].start();
      }

      // simply waiting for any one thread to stop main thread from exiting.
      students[0].join();
   }
}

class Student implements Runnable {
   Library library;
   Random random;

   Student(Library library) {
      this.library = library;
      random = new Random();
   }

   public void run() {
      while (true) {
         int studentId = Integer.parseInt(Thread.currentThread().getName());
         int bookId = random.nextInt(library.books.length);
         try {
            library.allocateBook(studentId, bookId);

            // Simulate reading time
            Thread.sleep(3000);

            library.deallocateBook(studentId, bookId);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
   }
}

class Library {
   Semaphore[] books;

   // Assumption: books[0]=A, books[1]=B ...
   Library(Semaphore[] books) {
      this.books = books;
   }

   public void allocateBook(int studentId, int bookId) throws InterruptedException {
      books[bookId].acquire();
      System.out.println("Student " + studentId + " borrows Book " + bookId);
   }

   public void deallocateBook(int studentId, int bookId) {
      System.out.println("Student " + studentId + " returns Book " + bookId);
      books[bookId].release();
   }
}
