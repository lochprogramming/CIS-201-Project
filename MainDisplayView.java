import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;

public class MainDisplayView {
   // the class that makes up the display view
   
   private MainDisplayController controller;
   
   @FXML
   private TableView<Vocabulary> vocabTable;
   
   public MainDisplayView() {
      //setDefaultColumnSize();
   }
   
   public void setController(MainDisplayController c) {
      this.controller = c;
   }
   
   public MainDisplayController getController() {
      return controller;
   }
   
   private void setDefaultColumnSize() {
      double[] widths = {23.0, 17.0, 60.0};//define the width of the columns

      //set the width to the columns
      for (int i = 0; i < widths.length; i++) {
         vocabTable.getColumns().get(i).prefWidthProperty().bind(
            vocabTable.widthProperty().multiply(widths[i] / 100.0));
      }
   }
   
   @FXML
   public void closeMenuAction(ActionEvent event) { //Closes the program if the exit option is selected from the menu.
      controller.closeProgram();
   }
   
   @FXML
   public void openExportFileMenuAction(ActionEvent event) { // triggers an open file dialog for choosing an Anki export file
      controller.openExportFile();
   }

}