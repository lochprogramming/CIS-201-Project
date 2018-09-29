public class Vocabulary {
   // class for the vocabulary data structure
   
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
   
   public String toString() {
      return word + " - (" + partOfSpeech + ") " + definition;
   }
}
   
   