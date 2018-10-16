// **********************************************************************************
// Title: Major Project: AnkiDataReader
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: AnkiDataReader.java
// Description: Class that reads in vocabulary data from a Anki export file which uses
//              the subs2srs template
// **********************************************************************************

// Still needs a quality assurance pass to ensure that if it encounters cards that aren't
// formatted correctly it doesn't crash.  I've not tested it with cards that aren't in
// the correct format.

import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
//import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;


public class AnkiDataReaderTask extends Task<ObservableList<Entry>>{
   // class that reads in data from an Anki export file.
   
   // Some required objects as well as the string tags used in Anki's export files.
   private final File ankiFile;
   
   private final String EXPRESSION_FLAG = "<div class='expression'>";
   private final String NOTES_FLAG = "<div class='notes'>";
   private final String END_FLAG = "</div>";
   private final String VOCAB_SEPARATOR = " - ";
   private final String NEW_WORD_FLAG = "<div>";
   private final String FURIGANA_FLAG = "<ruby>";
   private final String FURIGANA_END_FLAG = "</ruby>";
   private final String LINE_BREAK = "<br />";
   
   
   public AnkiDataReaderTask(File file) {
      this.ankiFile = file;
   }
   
   //public ObservableList<Entry> readData() {
   @Override protected ObservableList<Entry> call() throws Exception {
      // main function that reads vocabulary data out of an Anki export file.
      // calls various helper functions to collect data into an ObservableList which it then 
      // sends back to the controller so it can update the data model.
      ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
      double fileSize = (double) ankiFile.length();
      double totalSizeRead = 0.0;
      
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ankiFile), StandardCharsets.UTF_8));
         String s;
         while ((s = br.readLine()) != null) {// reads in one card of data at a time until end of file.
            totalSizeRead += (double) s.getBytes(StandardCharsets.UTF_8).length;
            updateProgress(totalSizeRead, fileSize); // updates the tasks progress with value totalSizeRead / fileSize

            String expression = new String(extractExpression(s)); // calls function to extract the expression data
                                                                  // this is the sentence the vocabulary is used in
                                                                  
            ArrayList<Vocabulary> vocab = extractVocab(s); // calls function to extract an arraylist of Vocabulary from the card
            
            ArrayList<Entry> entries = createEntries(vocab, expression); // calls function to create Entry objects from the Vocabulary
            dictionaryEntries.addAll(entries); // calls function that adds the entries to the ObservableList   
         }
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Error in reading Anki File");
      }
      
      
      System.out.println(dictionaryEntries); // print for debugging purposes
      return dictionaryEntries;
   }
   
////////////////////////////////////////////////////////////////////////////////////////
//Helper functions
  
   private String extractExpression(String s) {
      // helper function that extracts the expression from the current card
      String expression = "";
      int expressionIndex = s.indexOf(EXPRESSION_FLAG);
      if (expressionIndex != -1) 
         expression =  s.substring(expressionIndex + EXPRESSION_FLAG.length(), s.indexOf(END_FLAG, expressionIndex));
      return expression;
   }
   
   
   private ArrayList<Vocabulary> extractVocab(String s) {
      ArrayList<Vocabulary> vocab = new ArrayList<Vocabulary>();
      
      int notesIndex = s.indexOf(NOTES_FLAG);
      if (notesIndex != -1) {
         String notesContent = s.substring(notesIndex + NOTES_FLAG.length(), s.lastIndexOf(END_FLAG) + END_FLAG.length());
         String[] lines = new String[10];
         
         //initialize the string array
         for (int i = 0; i < lines.length; i++)
            lines[i] = "";
         
         int count = 0;
         try {
            while (notesContent.length() != 0) {
               int[] flagInfo = nextFlag(notesContent); // grabs info about next flag location
               
               if (notesContent.indexOf(FURIGANA_FLAG) != -1) // parses the furigana in the word
                  //notesContent = parseFurigana(notesContent); //choose this option to keep furigana
                  notesContent = parseWithoutFurigana(notesContent); // chose this option to remove the furigana.
               else if (flagInfo[0] == 0) // checks if there was a non-furigana flag and removes it
                  notesContent = notesContent.substring(flagInfo[1]);
               else { // removes the current vocab line and stores it in an array
                   lines[count] = notesContent.substring(0, nextFlag(notesContent)[0]);
                   count++;
                   notesContent = notesContent.substring(nextFlag(notesContent)[0]);
               }
            }
         } catch (Exception e) {
            System.out.println("Error in extractVocab");
            System.out.println(notesContent);
         }
         
         
         String[][] vocabStrings = splitLines(lines); // split the lines into word, part of speech, definition
                                                      // and store result in an array
         
         
         // loop through the array and create new vocabulary from all the non-empty entries
         for (int i = 0; i < vocabStrings.length; i++) 
            if (vocabStrings[i][0].length() != 0)
               vocab.add(new Vocabulary(vocabStrings[i][0], vocabStrings[i][1], vocabStrings[i][2]));
               
         /*
         // old lines for calling the convertToVocabulary method
         // originally, the programmed created Vocabulary one entry at a time
         // replaced to force inclusion of 2D arrays
         // I haven't tested which method is more efficient, but I feel like this one is better
         for (int i = 0; i < lines.length; i++) 
            if (lines[i].length() != 0) 
               vocab.add(convertToVocabulary(lines[i]));
         */      
         
      }
      
      return vocab;
   }
   
   private int[] nextFlag(String s) {
      // returns an int array
      // the first entry is the index of the next flag character: <div> </div> <ruby> <br />
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
      
      return vocabStrings;
   }
   
   
   // original methods that are a little more elegant in my opinion.
   // deactivated to force to use of multi-dimensional arrays
   /*
   private Vocabulary convertToVocabulary(String line) {
      // helper function that takes a line of text containing vocab information for a single word
      // and converts it into a new Vocabulary object.
     
      String word = "", partOfSpeech = "", definition = "";
      // strips aparts text lines in the following formats:
      // word - (part of speech) definition
      // word - definition
      
      int wordEnd = line.indexOf(VOCAB_SEPARATOR); // find the separator string " - " that 
                                                   // separates the words from the rest of the entry
      try {                                             
         if (wordEnd != -1) { // there was a word separator
            word = line.substring(0, wordEnd);
            int posStart = line.indexOf(VOCAB_SEPARATOR + "("); // find the start of the part of speech
            
            if (posStart != -1) { // there was a part of speech included
               int defStart = line.indexOf(")", posStart); // find the start of the definition
                     
               if (defStart != 1) { // checking to make sure the ) was present.  If not, then there is an error in the formatting.
                  partOfSpeech = line.substring(posStart + (VOCAB_SEPARATOR + "(").length(), defStart).trim();
                  definition = line.substring(defStart + 1).trim();
               } else {
                  System.out.println("Error in splitLines");
                  System.out.println("No ending ) with part of speech detected");
                  System.out.println(line);
               }
            } else // there was no part of speech, so the rest is just the definition
               definition = line.substring(wordEnd + VOCAB_SEPARATOR.length()).trim();
         } else // this case triggers if the line was only the vocab word. This should never happen in practice
            word = line.trim();
            
      } catch (Exception e) {
         System.out.println("Error in convertToVocabulary");
         System.out.println(e);
         System.out.println(line);
      }

      return new Vocabulary(word, partOfSpeech, definition);
   }
   */
   
   private ArrayList<Entry> createEntries(ArrayList<Vocabulary> vocab, String expression) {
      // helper function that creates Entry objects from each of the elements of a Vocabulary list.
      
      ArrayList<Entry> entries = new ArrayList<Entry>();
      for (int i = 0; i < vocab.size(); i++) {
         entries.add(new Entry(vocab.get(i), expression));
      }
      return entries;
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