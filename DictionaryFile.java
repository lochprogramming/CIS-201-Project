// **********************************************************************************
// Title: Major Project: DictionaryFile Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: DictionaryFile.java
// Description: Class that basically creates an observable File object.
//              Will eventually be used to monitor closing without saving events.
// **********************************************************************************

import javafx.beans.property.SimpleBooleanProperty;
import java.io.File;

public class DictionaryFile extends File{
   // Just a data object that contains the current file
   // and an observable property pertaining to whether it's
   // been changed since it was last saved.
   
   //private File file;
   private SimpleBooleanProperty changedSinceSave;
   
   public DictionaryFile(String s) {
      super(s);
      this.changedSinceSave = new SimpleBooleanProperty(false);
   }
   
   public DictionaryFile(File file) {
      super(file.getPath());
      this.changedSinceSave = new SimpleBooleanProperty(false);
   }
   
   public void setChangedSinceSave(boolean changed) {
      this.changedSinceSave.set(changed);
   }
   
   public boolean isChangedSinceSave() {
      return changedSinceSave.get();
   }
}