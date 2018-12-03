// **********************************************************************************
// Title: Major Project: AnkiDataReaderTask
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: AnkiDataReaderTask.java
// Description: Class that reads in vocabulary data from a Anki export file which uses
//              the subs2srs template
// **********************************************************************************

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.concurrent.Task;
import java.util.List;

public class AnkiDataReaderTask extends Task<ObservableList<Entry>>{
   // class that reads in data from an Anki export files.
   // this class defines a task that is meant to be run on a thread separate from the JavaFX thread
   
   // Some required objects as well as the string tags used in Anki's export files.
   private final List<File> ANKIFILES;
   private double fileSize;
   private double totalSizeRead;
   
   // below are various strings uses in the Anki output file (it uses some xml-type coding)
   // also includes some artifacts from the website the entires were copied from,
   // some of which are non-printed UTF-8 characters.
   private final String EXPRESSION_FLAG = "<div class='expression'>";
   private final String EXPRESSION_BREAK = "&nbsp";
   private final String NOTES_FLAG = "<div class='notes'>";
   private final String END_FLAG = "</div>";
   private final String VOCAB_SEPARATOR = " - ";
   private final String NEW_WORD_FLAG = "<div>";
   private final String FURIGANA_FLAG = "<ruby>";
   private final String FURIGANA_END_FLAG = "</ruby>";
   private final String LINE_BREAK = "<br />";
   
   
   public AnkiDataReaderTask(List<File> files) {
      // constructor that stores the files to be imported and compiles the total read size for the task
      // size data is used for the updateProgress task method.
      fileSize = 0.0;
      totalSizeRead = 0.0;
      this.ANKIFILES = files;
      
      // computes the total file size
      for (int i = 0; i < ANKIFILES.size(); i++)
         this.fileSize += (double) this.ANKIFILES.get(i).length();
   }
   
   @Override 
   protected ObservableList<Entry> call() throws Exception {
      // main function that reads vocabulary data out of Anki export files.
      // calls various helper functions to collect data into an ObservableList which it then 
      // sends back to the controller so it can update the data model.
      
      ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
      
      // function loops through and reads each file
      for (int i = 0; i < ANKIFILES.size(); i++)
         dictionaryEntries.addAll(readFile(ANKIFILES.get(i)));
      
      return dictionaryEntries;
   }
   
////////////////////////////////////////////////////////////////////////////////////////
//Helper functions
   
   private ObservableList<Entry> readFile(File ankiFile) {
      // method that extracts entries from an ankifile
      
      ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
      
      // a bufferedreader wrapping an inputstreadreader and a fileinputstream allows the reading of UTF-8 characters
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ankiFile), StandardCharsets.UTF_8))) {
         // using a try-with-resources that will auto-close the buffered reader when the program is done with the try block.
         String s;
         while ((s = br.readLine()) != null) {// reads in one card of data at a time until end of file.
            totalSizeRead += (double) s.getBytes(StandardCharsets.UTF_8).length; // determined how big the current line is
            updateProgress(totalSizeRead, fileSize); // updates the tasks progress with value totalSizeRead / fileSize
            
            // calls extractExpression to extract the expression data this is the sentence the vocabulary is used in                                                              
            // calls extractVocab to extract an arraylist of Vocabulary from the card
            // calls createEntries to create Entry objects from the Vocabulary
            ArrayList<Entry> entries = createEntries(extractVocab(s), extractExpression(s));   
                      
            dictionaryEntries.addAll(entries); // adds the entries to the ObservableList   
         }
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Error in reading Anki File");
      }
      
      return dictionaryEntries;
   }
  
   private String extractExpression(String s) {
      // helper function that extracts the expression from the current card
      String expression = "";
      int expressionIndex = s.indexOf(EXPRESSION_FLAG);
      if (expressionIndex != -1) 
         expression =  s.substring(expressionIndex + EXPRESSION_FLAG.length(), s.indexOf(END_FLAG, expressionIndex));

      return removeExpressionBreaks(expression);
   }
   
   private String removeExpressionBreaks(String expression) {
      // recursive implementation to remove some line break artifacts from the expression.
      // EXPRESSION_BREAK is an html artifact from copying the information off an online dictionary
      
      int expressionIndex = expression.indexOf(EXPRESSION_BREAK);
      
      if (expressionIndex != -1)
         return removeExpressionBreaks(expression.substring(0, expressionIndex) 
                                          + expression.substring(expressionIndex + EXPRESSION_BREAK.length()));
      else
         return expression;
   }
   
   
   private ArrayList<Vocabulary> extractVocab(String s) {
      // method that extracts each vocabulary word from the card and makes an arraylist from them.
      // utilizes a recursive method to break down the initial string 
      // removing vocabulary information one at a time until it is empty.
      
      ArrayList<Vocabulary> vocab = new ArrayList<Vocabulary>();
      
      // vocabulary is contained in the notes section of the anki card data.
      int notesIndex = s.indexOf(NOTES_FLAG);
      if (notesIndex != -1) {
         // removes the text between the notes flags. 
         String notesContent = s.substring(notesIndex + NOTES_FLAG.length(), s.lastIndexOf(END_FLAG) + END_FLAG.length());
         
         // initializes a one-dimentional string array to hold the individual vocabulary text as they're pulled out.
         String[] lines = new String[10];
         for (int i = 1; i < lines.length; i++)
               lines[i] = "";
         
         int count = 0;
         try {             
            // cleans the notes content of tags and inserts /n characters between different vocabulary entries
            // stores the cleaned content in the first element of the string array.
            lines[0] = removeTags(notesContent); 
            separateLines(lines, 1); // splits the individual vocabulary entries and propogates them to the rest of the array elements
         } catch (Exception e) {
            System.out.println("Error in extractVocab");
            System.out.println(notesContent);
         }
         
         String[][] vocabStrings = splitLines(lines); // split the lines into word, part of speech, definition
                                                      // and store result in a two-dimensional array
         
         // loop through the array and create new vocabulary from all the non-empty entries
         for (int i = 0; i < vocabStrings.length; i++) 
            if (vocabStrings[i][0].length() != 0)
               vocab.add(new Vocabulary(vocabStrings[i][0], vocabStrings[i][1], vocabStrings[i][2]));     
      }
      
      return vocab;
   }
   
   private String removeTags(String s) {
      // recursive method to remove all the tags (<div> </div> <ruby> and <br />) from a string
      // puts a \n character between separate vocabulary entries
      // note: reverses the order the vocabulary entries appeared in the card
               
      int[] flagInfo = nextFlag(s); // grabs the index and length of the next flag character in the string
      
      if (flagInfo[0] == -1)
         return s; // no tags, so return the string
      else if (s.indexOf(FURIGANA_FLAG) != -1)  // parses out the furigana in the word
         //return removeTags(parseFurigana(s)); //choose this option to keep furigana
         return removeTags(parseWithoutFurigana(s)); // chose this option to remove the furigana.
      else if (flagInfo[0] == 0)
         return removeTags(s.substring(flagInfo[1])); // remove the tag from the front and recursively check for more
      else
         return removeTags(s.substring(flagInfo[0]) + "\n" + s.substring(0, flagInfo[0])); // relocate the part in front of the tag to the back
                                                                                           // and puts a \n character in front of it.
   }
   
   private String[] separateLines(String[] lines, int count) {
      // recursive method takes a string with separate vocab entries separated by \n characters
      // splits the entries into a string array
      // assumes count > 0
      
      // Takes the string in the index before count and checks to see if there is a \n character,
      // which indicates that there are multiple vocabulary in that line still.
      // If there is, it pulls out everything after the \n and stores it in the count index.
      // Then it replaces the entry in the count-1 index with everything before the \n character.
      if (lines[count - 1].indexOf("\n") == -1)
         return lines;
      else {
         lines[count] = lines[count - 1].substring(lines[count - 1].indexOf("\n") + "\n".length());
         lines[count - 1] = lines[count - 1].substring(0, lines[count - 1].indexOf("\n"));
         return separateLines(lines, count + 1);
      } 
   }
   
   private int[] nextFlag(String s) {
      // returns an int array
      // the first entry is the index of the next flag character: <div> </div> <br />
      // the second entry is the length of that flag
      
      int[] flagInfo = {-1, 0};
      
      try{
         if (s.indexOf(NEW_WORD_FLAG) != -1) {
            flagInfo[0] = s.indexOf(NEW_WORD_FLAG);
            flagInfo[1] = NEW_WORD_FLAG.length();
         }
         if ((s.indexOf(END_FLAG) != -1) && ((s.indexOf(END_FLAG) < flagInfo[0]) || flagInfo[0] == -1)) {
            flagInfo[0] = s.indexOf(END_FLAG);
            flagInfo[1] = END_FLAG.length();
         }
         if ((s.indexOf(LINE_BREAK) != -1) && ((s.indexOf(LINE_BREAK) < flagInfo[0]) || flagInfo[0] == -1)) {
            flagInfo[0] = s.indexOf(LINE_BREAK);
            flagInfo[1] = LINE_BREAK.length();
         }

      } catch (Exception e) {
         System.out.println("Error in nextFlag");
         System.out.println(s);
      }
      
      return flagInfo;
   }
   
   private String parseFurigana(String s) {
      // parses the furigana for the kanji contained in the vocab word
      // parses the string to change the HTML tags surrounding the furigana to brackets: [ and ]
          
      try{
         boolean flag = false; // got through the string without finding anything needing to be parsed
         while (!flag) {
            if (s.indexOf(FURIGANA_FLAG) != -1)
               s = s.substring(0, s.indexOf(FURIGANA_FLAG)) + s.substring(s.indexOf(FURIGANA_FLAG) + FURIGANA_FLAG.length());
            else if (s.indexOf("<rb>") != -1)
               s = s.substring(0, s.indexOf("<rb>")) + s.substring(s.indexOf("<rb>") + "<rb>".length());
            else if (s.indexOf("</rb>") != -1)
               s = s.substring(0, s.indexOf("</rb>")) + s.substring(s.indexOf("</rb>") + "</rb>".length());
            else if (s.indexOf("<rt>") != -1)
               s = s.substring(0, s.indexOf("<rt>")) + "[" + s.substring(s.indexOf("<rt>") + "<rt>".length());
            else if (s.indexOf("</rt>") != -1)
               s = s.substring(0, s.indexOf("</rt>")) + "]" + s.substring(s.indexOf("</rt>") + "</rt>".length());
            else if (s.indexOf(FURIGANA_END_FLAG) != -1)
               s = s.substring(0, s.indexOf(FURIGANA_END_FLAG)) + s.substring(s.indexOf(FURIGANA_END_FLAG) + FURIGANA_END_FLAG.length());
            else
               flag = true;
         }
      } catch (Exception e) {
         System.out.println("Error in parseFurigana");
         System.out.println(s);
      }
      
      return s;
   }
   
   private String parseWithoutFurigana(String s) {
      // method that removes furigana from a vocab word that has it.
      
      s = parseFurigana(s); //first, parse the string to change the HTML tags surrounding the furigana to brackets: [ and ]
      
      while (s.indexOf("[") != -1) { // then remove everything between the brackets
         s = s.substring(0, s.indexOf("[")) + s.substring(s.indexOf("]") + 1);
      }
      
      return s;
   }
   
   private String[][] splitLines(String[] lines) {
      // helper method that takes the lines of text containing vocab information for a single word
      // and splits them into their three parts: word, part of speech, and definition.
      // information stored in a 2D String array.
      
      // set up the local variables we'll need.
      int size = lines.length;
      int parts = 3;
      String[][] vocabStrings = new String[size][parts];
      
      // initialize the 2D String array
      for (int i = 0; i < size; i++)
         for (int j = 0; j < parts; j++)
            vocabStrings[i][j] = "";
      
      // Loop through each of the lines and strip it apart
      // Strips aparts text lines in the following formats:
      // word - (part of speech) definition
      // word - definition
      for (int i = 0; i < size; i++) {
         if (lines[i].length() != 0) {
            int wordEnd = lines[i].indexOf(VOCAB_SEPARATOR); // find the separator string " - " that 
                                                             // separates the words from the rest of the entry
            int posStart = lines[i].indexOf(VOCAB_SEPARATOR + "("); // find the start of the part of speech
            
            try {
               if (wordEnd != -1) { // there was a separator
                  vocabStrings[i][0] = lines[i].substring(0, wordEnd).trim();
                  if (posStart != -1) { // there was a part of speech included
                     int defStart = lines[i].indexOf(")", posStart); // find the start of the definition
                     
                     if (defStart != 1) { // checking to make sure the ) was present.  If not, then there is an error in the formatting.
                        vocabStrings[i][1] = lines[i].substring(posStart + (VOCAB_SEPARATOR + "(").length(), defStart).trim();
                        vocabStrings[i][2] = lines[i].substring(defStart + 1).trim();
                     } else {
                        System.out.println("Error in splitLines");
                        System.out.println("No ending ) with part of speech detected");
                        System.out.println(lines[i]);
                     }
                  } else // there was no part of speech, so the rest is just the definition
                     vocabStrings[i][2] = lines[i].substring(wordEnd + VOCAB_SEPARATOR.length()).trim();
               } else // this case triggers if the line was only the vocab word. This should never happen in practice
                  vocabStrings[i][0] = lines[i].trim();
                    
            } catch (Exception e) {
               System.out.println("Error in splitLines");
               System.out.println("Error in formatting detected.");
               System.out.println(e);
               System.out.println(lines[i]);
            }
         }
      }
      
      //Iterate through the entries, and if the definition ends with byte -117 in UTF-8 encoding, then remove the last byte.
      //This is an artifact left over from copying stuff from a website when making the cards.
      //we need to do this to eliminate duplicates when adding things into the dictionary.
      
      for (int i = 0; i < vocabStrings.length; i++) {
         if (vocabStrings[i][2].length() != 0) {
            byte[] byteArray = vocabStrings[i][2].getBytes(StandardCharsets.UTF_8);
            if (byteArray[byteArray.length - 1] == -117)
               vocabStrings[i][2] = vocabStrings[i][2].substring(0, vocabStrings[i][2].length() - 1);
         }
      }
      
      return vocabStrings;
   }
      
   private ArrayList<Entry> createEntries(ArrayList<Vocabulary> vocab, String expression) {
      // helper function that creates Entry objects from each of the elements of a Vocabulary list.
      
      ArrayList<Entry> entries = new ArrayList<Entry>();
      for (int i = 0; i < vocab.size(); i++) 
         entries.add(new Entry(vocab.get(i), expression));
      
      return entries;
   }        
}

