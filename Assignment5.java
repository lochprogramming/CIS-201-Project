// **********************************************************************************
// Title: Major Project: Assignment5 Main File
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: Assignment5.java
// Description: Class containing the main method for the project.
// **********************************************************************************

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
 
public class Assignment5 extends Application {
   @Override
   public void start(final Stage primaryStage) {
      try {
         /*creates an instance of the appropriate scene loader
         creates a parent of for that scene
         gets the current stage
         changes the scene to the new one*/
         
         /*
         Design breakdown for program:
         1. The program view will be handled by the MainDisplayView class
         2. When the view is loaded, it will instantiate it's controller class: MainDisplayController
         3. User interactions in with the view will trigger event handlers that call methods in the MainDisplayController.
         4. The MainDisplayController will instantiate the data model
         5. The MainDisplayController will handle the program logic and call data model methods to alter the data model.
         6. The model will report changes to the controller which will in turn report changes to the view for updating. 
         
         The MainDisplayController will manage interactions between the program view and the data.
         When the user interacts with something in the view, the controller will be called upon to deal
            with that interaction.  It will then update the model, and pass back necessary updates to the view.
         */
         
         primaryStage.setTitle("Anki Vocabulary Extractor");
         primaryStage.show();
         
         
         FXMLLoader mainDisplayLoader = new FXMLLoader(getClass().getResource("MainDisplayView.fxml"));
         // Sets up the scene from the MainDisplayView for when program initially starts
         Scene scene = new Scene(mainDisplayLoader.load()); // 
         primaryStage.setTitle("Anki Vocabulary Extractor"); //Set the title for our program window
         primaryStage.setScene(scene); //Display SplashScreen window, using the scene graph
         //primaryStage.setMaximized(true);
         primaryStage.show();       
         
         //Set up a handler to deal with someone trying to close the window from non-menu means.
         primaryStage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            ((MainDisplayView) mainDisplayLoader.getController()).getController().closeWindow(primaryStage);
            event.consume();
         });
         
      } catch (IOException e) {
         e.printStackTrace();
         System.out.println("Error Loading MainDisplay");
      }
   }
   public static void main(String[] args) {
      //Launch the application
      launch(args);
   }
}