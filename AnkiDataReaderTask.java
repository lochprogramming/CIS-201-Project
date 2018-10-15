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
         
         for (int i = 0; i < lines.length; i++) 
            if (lines[i].length() != 0) 
               vocab.add(convertToVocabulary(lines[i]));
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
   
   private Vocabulary convertToVocabulary(String line) {
      // helper function that takes a line of text containing vocab information for a single word
      // and converts it into a new Vocabulary object.
      
      int wordEnd = line.indexOf(VOCAB_SEPARATOR);
      String word = "", partOfSpeech = "", definition = "";
      // strips aparts text lines in the following formats:
      // word - (part of speech) definition
      // word - definition
      
      if (wordEnd != -1) {
         try {
            word = line.substring(0, wordEnd);
            line = line.substring(wordEnd);
            
            if (line.indexOf(VOCAB_SEPARATOR + "(") != -1) {
               partOfSpeech = line.substring((VOCAB_SEPARATOR + "(").length(), line.indexOf(")"));
               line = line.substring(line.indexOf(")") + 1);
            } else
               line = line.substring(line.indexOf(VOCAB_SEPARATOR) + VOCAB_SEPARATOR.length());
            
            definition = line;
            
         } catch (Exception e) {
            System.out.println("Error in convertToVocabulary");
            System.out.println(e);
            System.out.println(line);
         }
      } else
         word = line;
      return new Vocabulary(word.trim(), partOfSpeech.trim(), definition.trim());
   }
   
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