// **********************************************************************************
// Title: Major Project: AboutDisplayView
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: AboutDisplayView.java
// Description: Class for the display of the About Dialog
// **********************************************************************************

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class AboutDialogView {
   
   
   private Stage aboutDialogViewStage;
   private MainDisplayController controller;
   
   public AboutDialogView() {
      //default constructor
   }
   
   public void setController(MainDisplayController controller) {
      this.controller = controller;
   }
   
   public void setAboutDialogViewStage(Stage stage) {
      this.aboutDialogViewStage = stage;
   }
   
   @FXML
   public void okButtonAction(ActionEvent event) {
      // button handler that asks the main controller to close the AboutDialogWindow
      controller.closeWindow(aboutDialogViewStage);
   }
}