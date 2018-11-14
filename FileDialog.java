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
import java.util.List;
import java.net.URISyntaxException;

public class FileDialog {
   // this class manages file choosing dialogs. 
   
   MainDisplayController controller;
   File currentDirectory;
   
   public FileDialog(MainDisplayController c) {
      this.controller = c;
      this.currentDirectory = null;
   }
   
   private void findCurrentDirectory() throws URISyntaxException {
      currentDirectory = new File(
               MainDisplayController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
   }
   
   public List<File> openExportFile() {
      //method which lets the user choose the export file from Anki that they wish to extract from.
      try {
         findCurrentDirectory();
      } catch (URISyntaxException e) {
         System.out.println("Could not find the current directory");
      }
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Anki Export File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
      if ((currentDirectory != null) && (currentDirectory.isDirectory()))
         fileChooser.setInitialDirectory(currentDirectory);
         
      return fileChooser.showOpenMultipleDialog(controller.getPrimaryStage());
   } 
   
   public DictionaryFile openDictionaryFile() {
      // method which lets the user choose a previously saved dictionary file containing
      // previously extracted vocabulary 
      
      try {
         findCurrentDirectory();
      } catch (URISyntaxException e) {
         System.out.println("Could not find the current directory");
      }
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Open Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
      if ((currentDirectory != null) && (currentDirectory.isDirectory()))
         fileChooser.setInitialDirectory(currentDirectory);
         
      File file = fileChooser.showOpenDialog(controller.getPrimaryStage());
      return (file != null) ? new DictionaryFile(file): null;
   }
   
   public DictionaryFile saveDictionaryFile() {
      // method which allows the user to choose a file to save the current dictionary information in.
      
      try {
         findCurrentDirectory();
      } catch (URISyntaxException e) {
         System.out.println("Could not find the current directory");
      }
      
      FileChooser fileChooser = new FileChooser(); // sets up the file chooser dialog
      fileChooser.setTitle("Save Dictionary File");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
      if ((currentDirectory != null) && (currentDirectory.isDirectory()))
         fileChooser.setInitialDirectory(currentDirectory);
         
      File file = fileChooser.showSaveDialog(controller.getPrimaryStage());
      return (file != null) ? new DictionaryFile(file): null;
   }
}