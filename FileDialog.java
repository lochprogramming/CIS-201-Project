// **********************************************************************************
// Title: Major Project: FileDialog Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: FileDialog.java
// Description: A helper class that will create various file selection dialogs.
//              Used for Open, Save, and Save As... dialogs.
// **********************************************************************************

import java.io.File;
import javafx.stage.FileChooser;

public class FileDialog {
   // this class manages file choosing dialogs. 
   
   MainDisplayController controller;
   
   public FileDialog(MainDisplayController c) {
      this.controller = c;
   }
   
   public File openExportFile() {
      //method which lets the user choose the export file from Anki that they wish to extract from.
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Anki Export File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
      return fileChooser.showOpenDialog(controller.getPrimaryStage());
   } 
   
   public DictionaryFile openDictionaryFile() {
      // method which lets the user choose a previously saved dictionary file containing
      // previously extracted vocabulary 
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma Delimited File (*.csv)", "*.csv"));
      return new DictionaryFile(fileChooser.showOpenDialog(controller.getPrimaryStage()));
   }
   
   public DictionaryFile saveDictionaryFile() {
      // method which allows the user to choose a file to save the current dictionary information in.
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Save Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma Delimited File (*.csv)", "*.csv"));
      return new DictionaryFile(fileChooser.showSaveDialog(controller.getPrimaryStage()));
   }
}