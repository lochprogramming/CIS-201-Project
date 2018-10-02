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

public class DictionaryFile {
   // Just a data object that contains the current file
   // and an observable property pertaining to whether it's
   // been changed since it was last saved.
   
   private File file;
   private SimpleBooleanProperty changedSinceSave;
   
   public DictionaryFile(String s) {
      this.file = new File(s);
      this.changedSinceSave = new SimpleBooleanProperty(false);
   }
   
   public DictionaryFile(File file) {
      this.file = file;
      this.changedSinceSave = new SimpleBooleanProperty(false);
   }
   
   public void setFile(File file) {
      this.file = file;
   }
   
   public void setChangedSinceSave(boolean changed) {
      this.changedSinceSave.set(changed);
   }
   
   public File getFile() {
      return this.file;
   }
   
   public boolean getSaveState() {
      return changedSinceSave.get();
   }
   public SimpleBooleanProperty getChangedSinceSaveProperty() {
      return changedSinceSave;
   }
}