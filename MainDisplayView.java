// **********************************************************************************
// Title: Major Project: MainDisplayView Class
// Author: Matthew Lochman
// Course Section: CIS201-HYB2 (Seidel) Fall 2018
// File: MainDisplayView.java
// Description: The view class manages the GUI that the user sees.
//              User requests and input are passed to the controller to distribute.
//              The view needs to interact with the controller to pass events like
//                 button clicks and menu actions.        
// **********************************************************************************

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.CheckBox;

public class MainDisplayView {
   // the class that makes up the display view
   
   private MainDisplayController controller;
      
   @FXML
   private TableView<Entry> vocabTable;
   
   @FXML
   private TableColumn<Entry, String> wordColumn;

   @FXML
   private TableColumn<Entry, String> partOfSpeechColumn;

   @FXML
   private TableColumn<Entry, String> infoColumn;
   
   @FXML
   private Label wordCountLabel;

   @FXML
   private ProgressBar progressBar;

   @FXML
   private CheckBox checkBox;
   
   public MainDisplayView() {
      // default constructor
   }
   
   public void setController(MainDisplayController c) {
      this.controller = c;
   }
   
   public MainDisplayController getController() {
      return controller;
   }
   
   public void bindProgressBar(ReadOnlyDoubleProperty d) {
      progressBar.progressProperty().bind(d);
   }
   
   public void unbindProgressBar() {
      progressBar.progressProperty().unbind();
      progressBar.progressProperty().set(0.0);
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
   public void openDictionaryFileMenuAction(ActionEvent event) { // triggers an open file dialog for choosing an Anki export file
      controller.openDictionaryFile();
   }
   
   @FXML
   public void aboutHelpMenuAction(ActionEvent event) { // triggers the opening of the about dialog from the help menu
      controller.openAboutDialog();
   }
   
   @FXML
   public void saveMenuAction(ActionEvent event) { // triggers the save feature of the file menu
      controller.saveDictionaryFile();
   }
   
   @FXML
   public void saveAsMenuAction(ActionEvent event) { // triggers the save as feature of the file menu
      controller.saveAsDictionaryFile();
   }
   
   public void setTable(ObservableList<Entry> vocabList) {
      // method which sets the passed entry list to be displayed in the vocab table.
      vocabTable.setItems(vocabList);
   }
   
   public void setWordCount(int count) {
      wordCountLabel.setText(Integer.toString(count));
   }
   
   public void setInfoColumn(String text, String property) {
      // method that changes the display of the Info Column to display the example sentences for the vocabulary
      // it also switches the mouse-over tooltip to display the definition
      
      infoColumn.setText(text);
      infoColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>(property));
      
      vocabTable.refresh(); // refresh the table to update the display/factories
   }
   
   @FXML
   public void checkBoxAction(ActionEvent event) {
      // tells the controller that the checkbox has been toggled
      
      controller.toggleCheckBox(checkBox.isSelected());
   }
   
   public void initialize() {
      setDefaultColumnSize(); // links the columns to the window width so they stay in the same proportion when the window is resized.
      wordCountLabel.setText("");    
      
      // setup a row factory to create tooltips for each row in the tableview.
      // tooltip will display the example sentence
      vocabTable.setRowFactory(row -> new TableRow<Entry>() {
         private Tooltip tooltip = new Tooltip();
         @Override
         public void updateItem(Entry entry, boolean empty) {
            super.updateItem(entry, empty);
            if (empty || entry == null) {
               setTooltip(null);
            } else {
               if (checkBox.isSelected())
                  tooltip.setText("Definition: " + entry.getDefinition()); 
               else
                  tooltip.setText("Example sentence: " + entry.getSentence());
                  
               setTooltip(tooltip);
            }
         }
      });
      
      // initialize the cell factories that will extract the appropriate attributes for each column in the vocabulary table
      wordColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("word"));
      partOfSpeechColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("partOfSpeech"));   
      setInfoColumn("Definition", "definition"); // initialize the info column to display the definition
   }
}