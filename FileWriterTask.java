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

   private final static String SEP = "\t";
   
   private final File file;
   private final ObservableList<Entry> dictionary;
   
   public FileWriterTask(File file, ObservableList<Entry> dictionary) {
      this.file = file;
      this.dictionary = dictionary;
   }
   
   @Override protected Void call() throws Exception {
      int total = dictionary.size();
      int count = 0;
      
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(file), StandardCharsets.UTF_8));
                  
      bw.append("Word" + SEP + "Part of Speech" + SEP + "Definition" + SEP + "Sentence");
      bw.newLine();
      
      // loop through all the entries in the dictionary
      for (int i = 0; i < total; i++) {
         bw.write(writeLine(dictionary.get(i))); // write the line for the current entry
         bw.newLine(); // write a new line character to the buffer
         bw.flush(); // commit the contents of the buffered writer to the file
         updateProgress(++count, total); // update the task progress
      }
      
      bw.close(); // close the buffered writer
      
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