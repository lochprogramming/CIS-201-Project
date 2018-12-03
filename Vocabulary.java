// **********************************************************************************
// Title: Major Project: Vocabulary Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: Vocabulary.java
// Description: Class for the Vocabulary object. 
//              Contains attributes for word, part of speech, and definition
// **********************************************************************************

public class Vocabulary {
   // class for the vocabulary data structure
   
   // vocabular consists of three attributes
   private String word;
   private String partOfSpeech;
   private String definition;
   
   public Vocabulary() {
      this.word = "";
      this.partOfSpeech = "";
      this.definition = "";
   }
   
   public Vocabulary(String w, String pos, String def) {
      this.word = w;
      this.partOfSpeech = pos;
      this.definition = def;
   }
   
   public void setWord(String w) {
      this.word = w;
   }
   
   public void setPartOfSpeech(String pos) {
      this.partOfSpeech = pos;
   }
   
   public void setDefinition(String def) {
      this.definition = def;
   }
   
   public String getWord() {
      return word;
   }
   
   public String getPartOfSpeech() {
      return partOfSpeech;
   }
   
   public String getDefinition() {
      return definition;
   }
   
   @Override
   public String toString() {
      // returns a string formatted as "word - (part of speech) definition"
      return word + " - (" + partOfSpeech + ") " + definition;
   }
   
   // the following two methods are used to test for duplicates
   @Override
   public boolean equals(Object obj) {
      // override the equals method for Object to just check if the word and definition matches
      // examples of polymorphism
      
      if (!(obj instanceof Vocabulary))
         return false;
      else {
         if ((this.getWord().equals(((Vocabulary) obj).getWord())) && (this.getDefinition().equals(((Vocabulary) obj).getDefinition())))
            return true;
         else 
            return false;
      }
   }
   
   @Override
   public int hashCode() {
      // return the hashCode of the word
      
      return word.hashCode();
   }
}
   
   