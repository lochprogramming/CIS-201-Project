// **********************************************************************************
// Title: Major Project: FileWriterTask
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: FileWriterTask.java
// Description: Class that writes the dictionary data to a tab separated text file
// **********************************************************************************

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.concurrent.Task;

public class FileWriterTask extends Task<Void>{

   private final File FILE;
   private final ObservableList<Entry> DICTIONARY;
   private final static String SEP = "\t";
   
   public FileWriterTask(File file, ObservableList<Entry> dictionary) {
      this.FILE = file;
      this.DICTIONARY = dictionary;
   }
   
   @Override protected Void call() throws Exception {
      int total = DICTIONARY.size();
      int count = 0;
      
      try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
         // using a try-with-resources that will auto-close the buffered writer when the program is done with the try block.
         
         bw.append("Word" + SEP + "Part of Speech" + SEP + "Definition" + SEP + "Sentence");
         bw.newLine();
         
         // loop through all the entries in the dictionary
         for (int i = 0; i < total; i++) {
            bw.write(writeLine(DICTIONARY.get(i))); // write the line for the current entry
            bw.newLine(); // write a new line character to the buffer
            bw.flush(); // commit the contents of the buffered writer to the file
            updateProgress(++count, total); // update the task progress
         }
      } catch (Exception e) {
         System.out.println("Error in writing file.");
      }
      
      return null;
   }
   
   private String writeLine(Entry entry) {
      // helper method to construct the write line for a single entry
      String result = "";
      
      result += entry.getWord() + SEP;
      result += entry.getPartOfSpeech() + SEP;
      result += entry.getDefinition() + SEP;
      result += entry.getSentence();
      
      return result;
   }
}