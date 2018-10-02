// **********************************************************************************
// Title: Major Project: MainDisplayView Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: MainDisplayView.java
// Description: Class which monitors the program view what interacts with the user.
//              Anytime the user interacts with the view, calls the appropriate controller method
//              Updates the view in response to calls from the controller.
// **********************************************************************************

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;

public class MainDisplayView {
   // the class that makes up the display view
   
   private MainDisplayController controller;
   
   @FXML
   private TableView<Vocabulary> vocabTable;
   
   public MainDisplayView() {
      // default constructor
   }
   
   public void setController(MainDisplayController c) {
      this.controller = c;
   }
   
   public MainDisplayController getController() {
      return controller;
   }
   
   private void setDefaultColumnSize() {
      // method to ensure that the columns in the table are resized appropriately
      // when the window is resized.
      double[] widths = {23.0, 17.0, 60.0};//define the width of the columns

      //set the width to the columns
      for (int i = 0; i < widths.length; i++) {
         vocabTable.getColumns().get(i).prefWidthProperty().bind(
            vocabTable.widthProperty().multiply(widths[i] / 100.0));
      }
   }
   
   @FXML
   public void closeMenuAction(ActionEvent event) { //Closes the program if the exit option is selected from the File menu.
      controller.closeWindow(controller.getPrimaryStage());
   }
   
   @FXML
   public void openExportFileMenuAction(ActionEvent event) { // triggers an open file dialog for choosing an Anki export file
      controller.openExportFile();
   }
   
   @FXML
   public void aboutHelpMenuAction(ActionEvent event) { // triggers the opening of the about dialog from the help menu
      controller.openAboutDialog();
   }
}