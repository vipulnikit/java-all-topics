package com.scaler.lld.multithreading;

public class Test {
   public static void main(String[] args) {
      Print<A> print = new Print<>();
      A a = new A();
      print.setPrintValue(a);
      System.out.println(print.getPrintValue());
   }
}
class A extends ParentClass implements Interface1, Interface2, Interface3 {
   // Class implementation
}

class Print<T extends ParentClass & Interface1 & Interface2> {
   T value;

   public T getPrintValue() { return value; }

   public void setPrintValue(T value) { this.value = value; }
}

class ParentClass {

}

interface  Interface1 {

}

interface Interface2 {

}

interface Interface3 {

}