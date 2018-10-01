import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;

public class DictionaryModel {
   // dictionary data model class
   private ObservableList<Entry> dictionaryEntries = FXCollections.observableArrayList();
   private DictionaryFile currentFile;
   
   public DictionaryModel() {
   
   }  
   
   public ObservableList<Entry> getDictionaryEntries() {
      return dictionaryEntries;
   }
   
   public void addEntry(Entry e) {
      dictionaryEntries.add(e);
   }
   
   public void setCurrentFile(DictionaryFile file) {
      currentFile.setFile(file.getFile());
      currentFile.setChangedSinceSave(true);
   }
   
   public DictionaryFile getCurrentFile() {
      return currentFile;
   }
}