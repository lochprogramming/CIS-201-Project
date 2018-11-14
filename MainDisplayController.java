// **********************************************************************************
// Title: Major Project: MainDisplayController Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: MainDisplayController.java
// Description: The controller processes user requests. 
//              Based on the user request, the controller calls methods in the view
//              and model to accomplish the requested action.
// **********************************************************************************
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import java.io.File;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.stage.Modality;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.EventHandler;
import javafx.concurrent.WorkerStateEvent;

public class MainDisplayController {
   // controller for the main program
   // interacts with the main display view and the dictionary model
   
   private MainDisplayView view; // current program view visible to user
   private DictionaryModel model; // current instance of the dictionary data model
   private Stage primaryStage; //For referencing the main window
   
   public MainDisplayController(MainDisplayView view, DictionaryModel model) {
      this.view = view;
      this.model = model;
      view.setController(this);
      view.setTable(model.getDictionaryEntries());
      
      //Add a Listener to the model's data list that checks to see if it has been changed.
      //If so, it will update the word count in the display.
      //This listener will be the way the view updates the information displayed to the user.
      model.addListener(new ListChangeListener<Entry>() {
         @Override
         public void onChanged(Change<? extends Entry> c) {
            view.setWordCount(model.getDictionarySize());
         }
      });
   }
   
   public void setPrimaryStage(Stage stage) {
      this.primaryStage = stage;
   }
   
   public void setModel(DictionaryModel model) {
      this.model = model;
   }
   
   public void setView(MainDisplayView view) {
      this.view = view;
   }
   
   public DictionaryModel getModel() {
      return model;
   }
   
   public MainDisplayView getView() {
      return view;
   }
   
   public Stage getPrimaryStage() {
      return this.primaryStage;
   }
   
   public void bindProgress(ReadOnlyDoubleProperty d) {
      view.bindProgressBar(d);
   }
   
   public void unbindProgress() {
      view.unbindProgressBar();
   }
   
   public void openExportFile() {
      //FileDialog fileDialog = new FileDialog(this);
      //File file = fileDialog.openExportFile();
      
      // File will eventually be chosen by user.  Currently auto loading 
      // an Anki export file to save time testing the program.
      //File file = new File("Flying Witch__Flying Witch Episode 1.txt"); 
      File file = new File("Subs2srs Cards.txt");
      //File file = new File("testCards.txt");
      if (file != null) {
         System.out.println("File chosen successfully.");
         
         //create a data reader task
         AnkiDataReaderTask ankiDataReaderTask = new AnkiDataReaderTask(file);
         bindProgress(ankiDataReaderTask.progressProperty()); // bind the tasks progress property to the progress bar in the UI
         
         // Add an event handler that triggers when the task completes.
         ankiDataReaderTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
             new EventHandler<WorkerStateEvent>() {
             @Override
             public void handle(WorkerStateEvent t) {
                 addEntryToModel(ankiDataReaderTask.getValue()); // adds entries to model
                 unbindProgress(); // unbinds the progress property now that the task has completed
             }
         });
         try {
            Thread th = new Thread(ankiDataReaderTask); // creates a new background thread to run the task on
            th.setDaemon(true);
            th.start();
         } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error creating new thread for anki data reader task.");
         }
         
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void addEntryToModel(Entry e) {
      // overloaded method to add single entry to data model
      int[] temp = model.addEntry(e);
      createEntryAlert(temp);
   }
   
   public void addEntryToModel(ObservableList<Entry> entries) {
      // overloaded method to add multiple entries to data model
      int[] temp = model.addEntry(entries);
      createEntryAlert(temp);
   }
   
   
   public void openDictionaryFile() {
      // Allows the user to choose a previously saved dictionary file.
      // Currently doesn't do anything else except update the file name in the model
      // Will eventually load data into the current model
      
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.openDictionaryFile();
      
      if (file != null) {
         DictionaryFileReaderTask dictionaryFileReaderTask = new DictionaryFileReaderTask(file); // example of polymorphism since this task takes File as argument
         bindProgress(dictionaryFileReaderTask.progressProperty()); // bind the tasks progress property to the progress bar in the UI
         
         // Add an event handler that triggers when the task completes.
         dictionaryFileReaderTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
             new EventHandler<WorkerStateEvent>() {
             @Override
             public void handle(WorkerStateEvent t) {
                 addEntryToModel(dictionaryFileReaderTask.getValue()); // adds entries to model
                 model.setCurrentFile(file); // sets the current file to the opened one.
                 unbindProgress(); // unbinds the progress property now that the task has completed
                 System.out.println("Task Finished");
             }
         });
         try {
            Thread th = new Thread(dictionaryFileReaderTask); // creates a new background thread to run the task on
            th.setDaemon(true);
            th.start();
         } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error creating new thread for anki data reader task.");
         }
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void saveAsDictionaryFile() {
      // Allows the user to create a tab delimited text file after choosingg a new save location      
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.saveDictionaryFile();
      
      if (file != null) {        
         writeFile(file);
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void saveDictionaryFile() {
      // Allows the user to create a tab delimited text file overwriting the current file saved in the model
      
      if (model.getCurrentFile() == null)
         saveAsDictionaryFile();
      else
         writeFile(model.getCurrentFile());
   }
   
   private void writeFile(DictionaryFile file) {
      // Example of polymorphism.  FileWriterTask takes a File type but we're passing a DictionaryFile subclass type to it.
      FileWriterTask fileWriterTask = new FileWriterTask(file, model.getDictionaryEntries());
      bindProgress(fileWriterTask.progressProperty()); // bind the tasks progress property to the progress bar in the UI
         
      // Add an event handler that triggers when the task completes.
      fileWriterTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
          new EventHandler<WorkerStateEvent>() {
          @Override
          public void handle(WorkerStateEvent t) {  
             model.setCurrentFile(file); // sets the current file to the written one.
             unbindProgress(); // unbinds the progress property now that the task has completed
             
             // create an alert to inform the user of the successful completion of the file writing
             Alert alert = new Alert(AlertType.INFORMATION, String.format("%s written successfully.", file));
             alert.showAndWait();
          }
      });
      try {
         Thread th = new Thread(fileWriterTask); // creates a new background thread to run the task on
         th.setDaemon(true);
         th.start();
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("Error creating new thread for file writing task.");
      }     
   }
   
   public void openAboutDialog() {
      // Opens a new window to display the About Dialog
      
      try{
         FXMLLoader aboutDialogLoader = new FXMLLoader(getClass().getResource("AboutDialogView.fxml"));
         // Sets up the scene from the MainDisplayView for when program initially starts
         Scene scene = new Scene(aboutDialogLoader.load()); //  
         Stage newWindow = new Stage(); // create a new window
         newWindow.setTitle("About Anki Vocabulary Extractor"); // sets the window title
         newWindow.setScene(scene); //sets the scene diplayed in the window
         newWindow.initOwner(primaryStage); // set owner to main window
         newWindow.initModality(Modality.WINDOW_MODAL); // can't click on main window while dialog is open
         newWindow.show(); //make window visible
         newWindow.centerOnScreen(); //center window
       
         AboutDialogView aboutDialogView = aboutDialogLoader.getController(); // creates an instance of the aboutdialogview and stores the one just loaded
         aboutDialogView.setAboutDialogViewStage(newWindow); // passes the Stage to it so it can be closed.
         aboutDialogView.setController(this);
      } catch (IOException e) {
         e.printStackTrace();
         System.out.println("Error Loading about dialog");
      }
   }
   
   private void createEntryAlert(int[] counts) {
      // creates a simple alert popup that informs the user of the entries that the system processed.
      String total = "Total number of entries:";
      String newEntries = "New entries added:";
      String duplicateEntries = "Duplicate entries ignored:";
      String text = String.format("%-28s%10d.\n%-29s%10d.\n%-28s%10d.\n", total, counts[0] + counts[1], 
                                       newEntries, counts[0], duplicateEntries, counts[1]);
      Alert alert = new Alert(AlertType.INFORMATION, text);
      alert.showAndWait();
   }
   
   public void toggleCheckBox(boolean checked) {
      // instructs the view to change the info column based on the whether the checkbox is toggled. 
      
      String text;
      String property;
      
      if (checked) {
         text = "Example Sentence";
         property = "sentence";
      } else {
         text = "Definition";
         property = "definition";
      }
      
      view.setInfoColumn(text, property);
   }
   
   public void closeWindow(Stage stage) {
      // method called when the user tries to close a portion of the program view
      // Will eventually check to see if there is unsaved data in the data model
      // and if so will prompt the user to save before exiting
      
      if ((model.getCurrentFile() == null) || (!model.getCurrentFile().isChangedSinceSave())) {
         // trigger a prompt for the user to input whether they want to save, continue without saving, or cancel.
      }
     
      if (stage == getPrimaryStage())
         Platform.exit(); // closes the main program view and closes the program thread
      else
         stage.close(); // closes a child window.
   }
   
}