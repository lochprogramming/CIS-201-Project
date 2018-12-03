// **********************************************************************************
// Title: Major Project: Text Stack Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: TextStack.java
// Description: Class defining a stack class for Strings.
// **********************************************************************************

public class StringStack {

   // this class is basically the GenericStack class from the textbook with some changes.
   // size-checking in the pop and peek methods avoid index out-of-bounds exceptions
   // For the program it should return an empty string by default, otherwise a generic class
   // might have been useful.
   
   private java.util.ArrayList<String> stack = new java.util.ArrayList<String>();
   
   public StringStack() {
      // no-arg constructor
   }
   
   public String pop() {
      // method pulls the item off the top of the stack (and removes it)
      // if there is nothing in the stack it returns the empty string
      String result;
      if (!stack.isEmpty()) {
         result = stack.get(stack.size() - 1);
         stack.remove(stack.size() - 1);
      } else 
         result = "";
      
      return result;
   }
   
   public String peek() {
      // method returns the item off the top of the stack without removing it
      // if there is nothing in the stack it returns the empty string
      String result;
      if (!stack.isEmpty()) {
         result = stack.get(stack.size() - 1);
      } else 
         result = "";
      
      return result;
   }
   
   public void push(String s) {
      // method adds an item to the top of the stack
      stack.add(s);
   }
   
   public void clearStack() {
      // method removes all items from the stack
      stack.clear();
   }
   
   @Override
   public String toString() {
      // method returns a list of items on the stack as a string.
      return "Stack: " + stack.toString();
   }
}