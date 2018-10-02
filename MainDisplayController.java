// **********************************************************************************
// Title: Major Project: MainDisplayController Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: MainDisplayController.java
// Description: Controller class which controls the program.  
//              Handles all interaction between the program views and the data model.
//              Anytime the user interacts with the program, the controller calls
//              the appropriate method, and when necessary updates the data model
//              and the view.
// **********************************************************************************

import javafx.application.Platform;
import java.io.File;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.stage.Modality;

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
      
      // File will eventually be chosen by user.  Currently auto loading 
      // an Anki export file to save time testing the program.
      File file = new File("Flying Witch__Flying Witch Episode 1.txt");
      if (file != null) {
         System.out.println("File chosen successfully.");
         AnkiDataReader ankiDataReader = new AnkiDataReader(this, file);
         ankiDataReader.readData();
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void openDictionaryFile() {
      // Allows the user to choose a previously saved dictionary file.
      // Currently doesn't do anything else except update the file name in the model
      // Will eventually load data into the current model
      
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.openDictionaryFile();
      if (file != null) {
         model.setCurrentFile(file); // sets the current file to the opened one.
         System.out.println("File chosen successfully.");
      } else 
         System.out.println("File not chosen successfully.");
   }
   
   public void saveDictionaryFile() {
      // Allows the user to create a csv file to save the data in the current model.
      // Actual file writing not currently implemented.
      
      FileDialog fileDialog = new FileDialog(this);
      DictionaryFile file = fileDialog.saveDictionaryFile();
      //add some commands for file writing.
      //create new DictionaryWriter class.
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
   
   public void closeWindow(Stage stage) {
      // method called when the user tries to close a portion of the program view
      // Will eventually check to see if there is unsaved data in the data model
      // and if so will prompt the user to save before exiting
      
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
      if (stage == getPrimaryStage())
         Platform.exit(); // closes the main program view and closes the program thread
      else
         stage.close(); // closes a child window.
   }
   
}