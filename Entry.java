// **********************************************************************************
// Title: Major Project: Entry Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: Entry.java
// Description: A subclass of the Vocabulary class which includes an example sentence.
// **********************************************************************************

public class Entry extends Vocabulary {
   // class extends the Vocabulary class by adding in  
   // an example sentence using the word.
   
   String sentence;
   
   public Entry() {
      super();
      sentence = "";
   } 
   
   public Entry(String word, String pos, String def) {
      super(word, pos, def);
      sentence = "";
   }
   
   public Entry(Vocabulary vocab, String sent) {
      super(vocab.getWord(), vocab.getPartOfSpeech(), vocab.getDefinition());
      this.sentence = sent;
   }
   
   public Entry(String word, String pos, String def, String sent) {
      super(word, pos, def);
      this.sentence = sent;
   }
   
   public void setSentence(String sent) {
      this.sentence = sent;
   }
   
   public String getSentence() {
      return this.sentence;
   }
   
   public String toString() {
      return super.toString();
   }
}