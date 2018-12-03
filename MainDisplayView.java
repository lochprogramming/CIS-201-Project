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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import java.awt.event.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import java.util.Comparator;
import javafx.beans.property.ReadOnlyObjectProperty;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class MainDisplayView {
   // the class that makes up the display view
   // contains handler methods that are triggered by the user interfacing with the UI
   
   private MainDisplayController controller;
   private StringStack searchTextStack = new StringStack();
   
      
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
   
   @FXML
   private TextField searchTextField;

   @FXML
   private Button clearSearchButton;
   
   @FXML
   private Label clearSearchLabel;
   
   public MainDisplayView() {
      // default constructor
   }
   
   // allows the controller to be registered with this instance of the display view. 
   // needed since the display view is being loaded by FXML
   public void setController(MainDisplayController c) {
      this.controller = c;
   }
   
   public void bindProgressBar(ReadOnlyDoubleProperty d) {
      progressBar.progressProperty().bind(d);
   }
   
   public void unbindProgressBar() {
      progressBar.progressProperty().unbind();
      progressBar.progressProperty().set(0.0);
   }
   
   public ReadOnlyObjectProperty<Comparator<Entry>> getTableComparatorProperty() {
      return vocabTable.comparatorProperty();
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
   private void closeMenuAction(ActionEvent event) { //Closes the program if the exit option is selected from the File menu.
      controller.closeWindow(controller.getPrimaryStage());
   }
   
   @FXML
   private void openExportFileMenuAction(ActionEvent event) { // triggers an open file dialog for choosing an Anki export file
      controller.openExportFile();
   }
   
   @FXML
   private void openDictionaryFileMenuAction(ActionEvent event) { // triggers an open file dialog for choosing an Anki export file
      controller.openDictionaryFile();
   }
   
   @FXML
   private void aboutHelpMenuAction(ActionEvent event) { // triggers the opening of the about dialog from the help menu
      controller.openAboutDialog();
   }
   
   @FXML
   private void saveMenuAction(ActionEvent event) { // triggers the save feature of the file menu
      controller.saveDictionaryFile();
   }
   
   @FXML
   private void saveAsMenuAction(ActionEvent event) { // triggers the save as feature of the file menu
      controller.saveAsDictionaryFile();
   }
   
   @FXML
   private void deleteContextMenuAction(ActionEvent event) {// the delete option in the right-click context menu used by the tableview
      controller.removeEntryFromModel(vocabTable.getSelectionModel().getSelectedItem());
   }
   
   @FXML
   public void searchTrigger(ActionEvent event) { 
      // triggers the filtering of the data if the user presses the enter key while focued on the textfield
      
      controller.searchDataModel(searchTextField.getText());
      refreshTable();
   }
   
   public void refreshTable() {
      // method that forces a refresh for the tableview
      vocabTable.refresh();
   }
   
   public void setTable(ObservableList<Entry> vocabList) {
      // method which sets the passed entry list to be displayed in the vocab table.
      vocabTable.setItems(vocabList);
   }
   
   public void setWordCount(String s) {
      wordCountLabel.setText(s);
   }
   
   public void setInfoColumn(String text, String property) {
      // method that changes the display of the Info Column to display the example sentences for the vocabulary
      // it also switches the mouse-over tooltip to display the definition
      
      infoColumn.setText(text);
      infoColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>(property));
      
      refreshTable(); // refresh the table to update the display/factories
   }
   
   @FXML
   public void checkBoxAction(ActionEvent event) {
      // tells the controller that the checkbox has been toggled
      
      controller.toggleCheckBox(checkBox.isSelected());
   }
   
   public void setSearchText(String s) {
      searchTextField.setText(s);
   }
   
   @FXML
   private void clearSearchButtonAction(ActionEvent event) {
      controller.clearSearch();
   }
   
   public void initialize() {
      // method that sets up the table view and links some of the parts of the UI together
      // fixes the column widths to the window dimensions
      // sets row and cell factories to link the table to the data model
      // sets up a listener to include the clear button on the search text field when something has been typed there
      // sets up a listener to automatically trigger a search if the user starts typing in the search bar and then stops for 1 second.
      
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
      
      // listener to have an x mark at the end of the search bar when there is text that can be cleared out
      // makes the x appear as well as an invisible button that has a cursor change effect.  
      // clicking the botton clears the text in the search box.
      searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue.equals("")) {
             clearSearchLabel.setVisible(false);
             clearSearchButton.setVisible(false);
          } else {
             clearSearchLabel.setVisible(true);    
             clearSearchButton.setVisible(true);
          }
      });
      
      // listener that triggers a search if there's been a 1 second wait between new text being entered in the search box
      // each time a character is typed, the text in the search bar is put on top of the textstack 
      // and a pausetransition is created with a 1 second duration.
      // After the second is up, the transition checks to see if the current text in the search bar 
      // matches the text on top of the stack (in other words the user stopped typing)
      // if it doesn't the pause transition fizzles, otherwise it triggers a search
      searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
          searchTextStack.push(searchTextField.getText());
          
          PauseTransition pause = new PauseTransition(Duration.seconds(1));
          pause.setOnFinished(event -> {
            if (searchTextField.getText().equals(searchTextStack.peek())) {
               searchTrigger(new ActionEvent());
               searchTextStack.clearStack();
            }     
          });
          pause.playFromStart();
      });
     
   }
}