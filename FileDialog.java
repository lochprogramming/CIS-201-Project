import java.io.File;
import javafx.stage.FileChooser;

public class FileDialog {
   // this class manages file choosing dialogs. 
   
   MainDisplayController controller;
   
   public FileDialog(MainDisplayController c) {
      this.controller = c;
   }
   
   public File openExportFile() {
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Anki Export File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
      return fileChooser.showOpenDialog(controller.getPrimaryStage());
   } 
   
   public DictionaryFile openDictionaryFile() {
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma Delimited File (*.csv)", "*.csv"));
      return new DictionaryFile(fileChooser.showOpenDialog(controller.getPrimaryStage()));
   }
   
   public DictionaryFile saveDictionaryFile() {
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Save Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma Delimited File (*.csv)", "*.csv"));
      return new DictionaryFile(fileChooser.showSaveDialog(controller.getPrimaryStage()));
   }
}