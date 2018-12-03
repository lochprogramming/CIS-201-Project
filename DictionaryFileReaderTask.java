// **********************************************************************************
// Title: Major Project: DictionaryFileReaderTask
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: AnkiDataReaderTask.java
// Description: Class that reads in vocabulary data from a previously saved dictionary file
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


public class DictionaryFileReaderTask extends Task<ObservableList<Entry>>{
   // class defines a task that is used to open a previous saved dicitonary file made by the program
   // task is meant to be run on a thread separate from the JavaFX thread
   // there is verification to check if the file was indeed a file made by the program.
   
   // the file is a tab-separated text file
   private final static String SEP = "\t";
   
   // Some required objects as well as the string tags used in Anki's export files.
   private final File FILE;
   
   public DictionaryFileReaderTask(File file) {
      this.FILE = file;
   }
   
   @Override protected ObservableList<Entry> call() throws Exception {
      // method will read in the vocabulary entries and update the task progress
      
      ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
      double fileSize = (double) FILE.length();
      double totalSizeRead = 0.0;
      
      // a bufferedreader wrapping an inputstreadreader and a fileinputstream allows the reading of UTF-8 characters
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8))) {
         // using a try-with-resources that will auto-close the buffered reader when the program is done with the try block.
         String s = br.readLine(); // consume in the header line in the file

         while ((s = br.readLine()) != null) {// reads in one line at a time until end of file.
            totalSizeRead += (double) s.getBytes(StandardCharsets.UTF_8).length;
            updateProgress(totalSizeRead, fileSize); // updates the tasks progress with value totalSizeRead / fileSize
            
            // creates a string array to store the word, part of speech, definition, and example sentence
            String[] line = new String[4];
            int count = 0;
            do {
               int tabLocation = s.indexOf(SEP);
               if (tabLocation != -1) {
                  line[count] = s.substring(0, tabLocation);
                  s = s.substring(tabLocation + SEP.length());
               } else {
                  line[count] = s;
               }
               
               count++;
             } while (count < 4);
             dictionaryEntries.add(new Entry(line[0], line[1], line[2], line[3]));   
         }
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Error in reading dictionary File");
      }
      
      return dictionaryEntries;
   }
}