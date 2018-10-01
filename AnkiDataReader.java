import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class AnkiDataReader {
   // class that reads in data from an Anki export file.
   
   // Some required objects as well as the string tags used in Anki's export files.
   private MainDisplayController controller;
   private File ankiFile;
   private ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
   private final String EXPRESSION_FLAG = "<div class='expression'>";
   private final String NOTES_FLAG = "<div class='notes'>";
   private final String END_FLAG = "</div>";
   private final String VOCAB_SEPARATOR = " - ";
   private final String NEW_WORD_FLAG = "<div>";
   
   
   public AnkiDataReader(MainDisplayController mdc, File file) {
      this.controller = mdc;
      this.ankiFile = file;
   }
   
   
   public void readData() {
      // main function that reads vocabulary data out of an Anki export file.
      // calls various helper functions to collect data into an ObservableList which it then 
      // sends back to the controller so it can update the data model.
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ankiFile), StandardCharsets.UTF_8));
         String s;
         while ((s = br.readLine()) != null) {// reads in one card of data at a time until end of file.
            String expression = new String(extractExpression(s)); // calls function to extract the expression data
                                                                  // this is the sentence the vocabulary is used in
            ArrayList<Vocabulary> vocab = extractVocab(s); // calls function to extract an arraylist of Vocabulary from the card
            ArrayList<Entry> entries = createEntries(vocab, expression); // calls function to create Entry objects from the Vocabulary
            addEntriesToList(entries); // calls function that adds the entries to the ObservableList
         }
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Error in reading Anki File");
      }
      System.out.println(dictionaryEntries);
   }
   
   private String extractExpression(String s) {
      // helper function that extracts the expression from the current card
      String expression = "";
      int expressionIndex = s.indexOf(EXPRESSION_FLAG);
      if (expressionIndex != -1) 
         expression =  s.substring(expressionIndex + EXPRESSION_FLAG.length(), s.indexOf(END_FLAG, expressionIndex));
      return expression;
   }
   
   private ArrayList<Vocabulary> extractVocab(String s) {
      // helper function that is in charge of extracting the text in the 'notes' section
      // and converting all of the words there into an array of Vocabulary objects
      ArrayList<Vocabulary> vocab = new ArrayList<Vocabulary>();
      try {
         int notesIndex = s.indexOf(NOTES_FLAG);
         if (notesIndex != -1) {
            String vocabList = s.substring(notesIndex + NOTES_FLAG.length(), s.lastIndexOf(END_FLAG));
            try{
            String[] lines = splitString(vocabList); // need to split the text in the 'notes' section in case there are multiple words
            for (int i = 0; i < lines.length; i++) {
               if ((lines[i] != null) && (lines[i].equals("") != true))
                  vocab.add(convertToVocabulary(lines[i])); // converts each line of text into a Vocabulary object 
                                                            // and adds it to the arraylist
            }
            }catch (Exception e){
               System.out.println("checking after vocabList is created");
               System.out.println(vocabList);
               System.out.println(e);
            }
         }
      
      } catch (Exception e) {
         System.out.println("Error in extractVocab");
         System.out.println(e);
      }
      return vocab;
   }
   
   private String[] splitString(String s) {
      // helper function which takes the text in the 'notes' section in the card
      // and splits each vocab word into it's own string.
      // these are returned as an array.
      // note: no card has more than 10 vocabulary
      // Proper flash card making assumes 1 new word per card
      // but sometimes I have up to 5.
      String[] lines = new String[10];
      int count = 0;
      int currentIndex = 0;
      try{
         int nextIndex = s.indexOf(NEW_WORD_FLAG);
         if (nextIndex != -1) {
            // this case is for when there are multiple vocab words in a single card
            // After the first word, the rest are surrounded in <div> </div> tags
            // We start by pulling off the first word
            lines[count] = s.substring(currentIndex, nextIndex);
            currentIndex = nextIndex;
            count++;
            
            // this while loop pulls out the rest that are surrounded by <div> and </div> tags
            while (s.indexOf(NEW_WORD_FLAG, currentIndex) != -1) {  
               nextIndex = s.indexOf(END_FLAG, currentIndex);
               lines[count] = s.substring(currentIndex + NEW_WORD_FLAG.length(), nextIndex);
               count++;
               currentIndex = nextIndex + END_FLAG.length();
            }
         } else {
            // this case is for when there are no additional words.
            // all tags have been removed by previous functions
            lines[count] = s.substring(currentIndex);
            count++;
         }
      } catch (Exception e) {
         System.out.println("Error in splitString");
         System.out.println(e);
      }
      return lines;
   }
   
   private Vocabulary convertToVocabulary(String line) {
      // helper function that takes a line of text containing vocab information for a single word
      // and converts it into a new Vocabulary object.
      
      int wordEnd = line.indexOf(VOCAB_SEPARATOR);
      String word = "", partOfSpeech = "", definition = "";
      
      try {
         word = line.substring(0, wordEnd);
         if (line.contains(" - (")) {
            partOfSpeech = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
            definition = line.substring(line.indexOf(')') + 1);
         } else {
            definition = line.substring(line.indexOf(VOCAB_SEPARATOR)+VOCAB_SEPARATOR.length());
         }
      } catch (Exception e) {
         System.out.println("Error in convertToVocabulary");
         System.out.println(e);
         System.out.println(line);
      }
      
      return new Vocabulary(word, partOfSpeech, definition);
   }
   
   private ArrayList<Entry> createEntries(ArrayList<Vocabulary> vocab, String expression) {
      // helper function that creates Entry objects from each of the elements of a Vocabulary list.
      
      ArrayList<Entry> entries = new ArrayList<Entry>();
      for (int i = 0; i < vocab.size(); i++) {
         entries.add(new Entry(vocab.get(i), expression));
      }
      return entries;
   }
   
   private void addEntriesToList(ArrayList<Entry> entries) {
      // helper function that adds each Entry object in the passes array to the Observable List
      
      dictionaryEntries.addAll(entries);
   }
         
}



//stuff for the file write
/*
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(new File("WriteTest.txt")), StandardCharsets.UTF_8));
bw.append("This is a Test\n");
         bw.newLine();
         bw.append(s);
         bw.close();
*/