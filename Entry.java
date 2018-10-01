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