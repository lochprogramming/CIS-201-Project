// **********************************************************************************
// Title: Major Project: AboutDisplayView
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: AboutDisplayView.java
// Description: Class for the display of the About Dialog
// **********************************************************************************

import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import javafx.scene.Scene;
import javafx.geometry.Pos;

public class AboutDialogView2 {
   
   
   private Stage aboutDialogViewStage;
   private MainDisplayController controller;
   
   private final AnchorPane pane = new AnchorPane();
   private final Label title = new Label("Anki Vocabulary Extractor");
   private final Label author = new Label("Matthew Lochman");
   private final Label section = new Label("CIS201-HYB2 Fall 2018");
   private final VBox vBox = new VBox();
   private final Separator sep = new Separator();
   private final Label description = new Label("The project will focus on allowing the user to extract "
                                             + "information from digital flash cards made in the program Anki.  "
                                             + "In particular, it will concentrate on extracting data from a user-specified field on each card. "
                                             + "The extracted data is assumed to be vocabulary and will then be displayed in a table format.  "
                                             + "The user will have the ability to export the extracted data into a CSV file format for use in "
                                             + "other spreadsheet programs or a PDF file for quick reference and searching.  Other information "
                                             + "will be extracted, such as part of speech.  Users will be able to select which Anki decks "
                                             + "information will be extracted from. The program will require the user to provide an export file "
                                             + "from Anki that corresponds to cards made using the subs2srs template.  The program will read "
                                             + "through the export file and save the ‘notes’ field data into an array and display it to the user "
                                             + "in a table. While reading the Anki export file, the program will attempt to extract vocabulary "
                                             + "using the format “word - (part of speech) definition” or, if no part of speech is included, "
                                             + "“word - definition”.The user will be able to save the extracted information into a CSV file or "
                                             + "pdf file for easy searching. The user will have the option to load previously created files and "
                                             + "add newly extracted entries (the program will check to see if an entry already exists to avoid duplication).  ");
   private final Button okButton = new Button("OK");
   private Scene scene;
   
   
   public AboutDialogView2(Stage stage, MainDisplayController controller) {
      this.aboutDialogViewStage = stage;
      this.controller = controller;
      
      buildLayout();
      
      this.scene = new Scene(pane);
   }
   
   public Scene getScene() {
      return scene;
   }
   
   private void buildLayout() {
      pane.prefHeight(400.0);
      pane.prefWidth(600.0);
      
      description.setWrapText(true);
      description.setAlignment(Pos.TOP_LEFT);      
      description.setLayoutX(14.0);
      description.setLayoutY(113.0);
      description.setPrefHeight(287.0);
      description.setPrefWidth(569.0);
      
      sep.setLayoutX(19.0);
      sep.setLayoutY(79.0);
      sep.setPrefHeight(9.0);
      sep.setPrefWidth(560.0); 
      vBox.setLayoutX(14.0);
      vBox.setLayoutY(14.0);
      vBox.setPrefHeight(65.0);
      vBox.setPrefWidth(560.0);
      title.setPrefHeight(20.0);
      title.setPrefWidth(140.0);
      author.setPrefHeight(20.0);
      author.setPrefWidth(140.0);
      section.setPrefHeight(20.0);
      section.setPrefWidth(140.0);
      okButton.setLayoutX(273.0);
      okButton.setLayoutY(361.0);
      okButton.setOnAction(e -> controller.closeWindow(aboutDialogViewStage));
      
      vBox.getChildren().addAll(title, author, section);
      pane.getChildren().addAll(vBox, sep, description, okButton);
   }
}