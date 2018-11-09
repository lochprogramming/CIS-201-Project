// **********************************************************************************
// Title: Major Project: DictionaryModel Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: DictionaryModel.java
// Description: The dictionary model class stores the data for the program.
//              The model is independent of the user interface and doesn't know
//                 if it's being used from a text-based, graphical or web-interface.
//              This model will house the list of dictionary entries and any methods
//                 that may change the list.
// **********************************************************************************

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.util.Comparator;
import javafx.collections.ListChangeListener;

public class DictionaryModel {
   // dictionary data model class
   private ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
   private DictionaryFile currentFile;
   
   public DictionaryModel() {
      //default constructor
   }  
   
   public ObservableList<Entry> getDictionaryEntries() {
      return dictionaryEntries;
   }
   
   public int getDictionarySize() {
      return dictionaryEntries.size();
   }
   
   public void addListener(ListChangeListener<Entry> listener) {
      dictionaryEntries.addListener(listener);
   }
   
   public int[] addEntry(Entry e) {
      // adds a single entry to the data model
      int[] entryCount = {0, 0};
      if (dictionaryEntries.contains(e)) {
         entryCount[1]++;
      } else {
         entryCount[0]++;
         dictionaryEntries.add(e);
         
         if ((currentFile != null) && (!currentFile.isChangedSinceSave()))
            currentFile.setChangedSinceSave(true); // sets the ChangedSinceSave property to true
      }

      return entryCount;
   }
   
   public int[] addEntry(ObservableList<Entry> entries) {
      // adds a collection of entries to the data model
      // uses the overloaded addEntry for single entries
      
      int[] entryCount = {0, 0};
      for (int i = 0; i < entries.size(); i++) {
         int[] tempCount = addEntry(entries.get(i));
         entryCount[0] += tempCount[0];
         entryCount[1] += tempCount[1];
      }
      
      return entryCount;
   }
   
   public void setCurrentFile(DictionaryFile file) {
      currentFile = file;
      currentFile.setChangedSinceSave(true);
   }
   
   public void sortList() {
      // method that will sort the dictionary entry list based on the word.
      Comparator<Entry> comparator = Comparator.comparing(Entry::getWord);
      FXCollections.sort(dictionaryEntries, comparator);
   }
   
   public DictionaryFile getCurrentFile() {
      return currentFile;
   }
}