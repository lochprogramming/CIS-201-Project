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
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList; 
import java.util.Comparator;
import javafx.beans.property.ObjectProperty;

public class DictionaryModel {
   // dictionary data model class
   private ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
   private DictionaryFile currentFile;
   private FilteredList<Entry> filteredDictionaryEntries = new FilteredList<Entry> (dictionaryEntries, Entry.entrySearchFilter(""));
   
   // model uses a SortedList to wrap the FilteredList to allow it to be changed since FilteredList is unmodifiable.
   // in particular, the FilteredList cannot be sorted when a user tries to sort a TableView by clicking on column headings
   // So wrapping it in a SortedList fixes this.  
   private SortedList<Entry> searchResultEntries = new SortedList<Entry> (filteredDictionaryEntries);
   
   
   public DictionaryModel() {
      //default constructor
   }  
  
//////////////////////////////////////////////////////////////////////////////////////
// accessor methods
   
   public DictionaryFile getCurrentFile() {
      return currentFile;
   }
   
   public ObservableList<Entry> getDictionaryEntries() {
      return dictionaryEntries;
   }
   
   public SortedList<Entry> getSearchResultEntries() {
      return searchResultEntries;
   }
   
   public int getDictionarySize() {
      return dictionaryEntries.size();
   }
   
   public int getSearchResultSize() {
      return filteredDictionaryEntries.size();
   }
   
   public ObjectProperty<Comparator<? super Entry>> getSortedListComparatorProperty() {
      return searchResultEntries.comparatorProperty();
   }
   
   public boolean isFiltered() {
      return (dictionaryEntries.size() != filteredDictionaryEntries.size());
   }
   
///////////////////////////////////////////////////////////////////////////////////////////
// mutator methods
   
   public void setCurrentFile(DictionaryFile file) {
      currentFile = file;
      currentFile.setChangedSinceSave(true);
   }

   public void setSearchString(String searchString) {
      filteredDictionaryEntries.setPredicate(Entry.entrySearchFilter(searchString));
   }
   
   public void addListener(ListChangeListener<Entry> listener) {
      filteredDictionaryEntries.addListener(listener);
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
   
   public void removeEntry(Entry e) {
      // method for removing an entry from the model
      dictionaryEntries.remove(e);
      
      if ((currentFile != null) && (!currentFile.isChangedSinceSave()))
         currentFile.setChangedSinceSave(true); // sets the ChangedSinceSave property to true
   }
}