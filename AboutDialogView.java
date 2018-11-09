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
   
   //Button.  Used to grab stage in next method.
   private Stage aboutDialogViewStage;
   private MainDisplayController controller;
   
   @FXML
   private Button okButton;
   
   public AboutDialogView() {
      //default constructor
   }
   
   public MainDisplayController getController() {
      return controller;
   }
   
   public Stage getAboutDialogViewStage() {
      return aboutDialogViewStage;
   }
   
   public void setController(MainDisplayController controller) {
      this.controller = controller;
   }
   
   public void setAboutDialogViewStage(Stage stage) {
      this.aboutDialogViewStage = stage;
   }
   
   @FXML
   public void okButtonAction(ActionEvent event) {
      controller.closeWindow(aboutDialogViewStage);
   }
}