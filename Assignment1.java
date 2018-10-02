// **********************************************************************************
// Title: Major Project: Assignment1 Main File
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: Assignment1.java
// Description: Class containing the main method for the project.
// **********************************************************************************

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
 
public class Assignment1 extends Application {
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
         2. The data model will be handled by the DictionaryModel Class
         3. The program controller will be handled by the MainDisplayController class
         
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
         
         
         // Sets up the MVC program structure
         DictionaryModel dictionary = new DictionaryModel(); // creates a new instance of the dictionary data model
         MainDisplayView view = mainDisplayLoader.getController(); // creates an instance of the program view
         MainDisplayController controller = new MainDisplayController(view, dictionary); // creates a new instance of the 
                                                                                         // controller passing it the 
                                                                                         // view and dictionary
                                                                                         
         controller.setPrimaryStage(primaryStage);//pass the stage reference to the mainController

         
         
         //Set up a handler to deal with someone trying to close the window from non-menu means.
         primaryStage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            controller.closeWindow(primaryStage);
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