import javafx.application.Platform;
import java.io.File;
import javafx.stage.Stage;

public class MainDisplayController {
   // controller for the main program
   // interacts with the main display view and the dictionary model
   
   private MainDisplayView view;
   private DictionaryModel model;
   private Stage primaryStage;//For referencing the main window
   
   public MainDisplayController(MainDisplayView view, DictionaryModel model) {
      this.view = view;
      this.model = model;
      view.setController(this);
      System.out.println();
   }
   
   public void setPrimaryStage(Stage stage) {
      this.primaryStage = stage;
   }
   
   public Stage getPrimaryStage() {
      return this.primaryStage;
   }
   
   public void openExportFile() {
      //FileDialog fileDialog = new FileDialog(this);
      //File file = fileDialog.openExportFile();
      File file = new File("Flying Witch__Flying Witch Episode 1.txt");
      if (file != null) {
         System.out.println("File chosen successfully.");
         AnkiDataReader ankiDataReader = new AnkiDataReader(this, file);
         ankiDataReader.readData();
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void openDictionaryFile() {
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.openDictionaryFile();
      if (file != null) {
         model.setCurrentFile(file); // sets the current file to the opened one.
         System.out.println("File chosen successfully.");
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void saveDictionaryFile() {
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.saveDictionaryFile();
      //add some commands for file writing.
      //create new DictionaryWriter class.
   }
   
   public void closeProgram() {
      /*
      // checks to see if there is unsaved information
      DataIO dataIO = new DataIO(mainController);
      boolean okToDoAction = false;
          
      if (getNeedsSaved()) {
         String s = dataIO.checkDataSaveState();
         
         if (s.equals("Yes"))
            okToDoAction = dataIO.createFileDialog("Save...");
         else if (s.equals("No"))
            okToDoAction = true;
      }
      else
         okToDoAction = true;   
      
      if (okToDoAction)
         Platform.exit();
      */
      Platform.exit();
   }
   
}