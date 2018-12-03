// **********************************************************************************
// Title: Major Project: Entry Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: Entry.java
// Description: A subclass of the Vocabulary class which includes an example sentence.
// **********************************************************************************

import java.util.function.Predicate;

public class Entry extends Vocabulary {
   // class extends the Vocabulary class by adding in  
   // an example sentence using the word.
   
   String sentence;
   
   // no-arg constructor with blank sentence
   public Entry() {
      super();
      sentence = "";
   } 
   
   // constructor taking the different parts of a Vocabulary object and no sentence
   public Entry(String word, String pos, String def) {
      super(word, pos, def);
      sentence = "";
   }
   
   // constructor taking the different parts of a vocabulary object with a sentence
   public Entry(String word, String pos, String def, String sent) {
      super(word, pos, def);
      this.sentence = sent;
   }
   
   // constructor taking a Vocabulary object with a sentence
   public Entry(Vocabulary vocab, String sent) {
      super(vocab.getWord(), vocab.getPartOfSpeech(), vocab.getDefinition());
      this.sentence = sent;
   }
   
   public void setSentence(String sent) {
      this.sentence = sent;
   }
   
   public String getSentence() {
      return this.sentence;
   }

   // predicate used for filtering out visible entries based on search text
   public static Predicate<Entry> entrySearchFilter(String searchString) { 
      return new Predicate<Entry>() {
         public boolean test(Entry e) {
            return (e.isVisible(searchString));
         }
      };
   }
   
   private boolean isVisible(String searchString) {
      // method that determines if the searchString is contained in any part of the entry
      boolean result = false;
      
      if ((searchString == "") || (this.getWord().contains(searchString)) || (this.getPartOfSpeech().contains(searchString)) 
               || (this.getDefinition().contains(searchString)) || (sentence.contains(searchString)))
         result = true;
      
      return result;
   }
   
   @Override
   public String toString() {
      return super.toString();
   }
}