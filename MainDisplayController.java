import javafx.application.Platform;

public class MainDisplayController {
   // controller for the main program
   // interacts with the main display view and the dictionary model
   
   MainDisplayView view;
   DictionaryModel model;
   
   public MainDisplayController(MainDisplayView view, DictionaryModel model) {
      this.view = view;
      this.model = model;
      System.out.println(this.view);
      System.out.println(this.model);
      System.out.println(this);
      view.setController(this);
   }
   
   public void closeProgram() {
      /*
      // checks to see if there is unsaved information
      DataIO dataIO = new DataIO(mainController);
      boolean okToDoAction = false;
          
      if (getNeedsSaved()) {
         String s = dataIO.checkDataSaveState();
         
         if (s.equals("Yes"))
            okToDoAction = dataIO.createFileDialog("Save...");
         else if (s.equals("No"))
            okToDoAction = true;
      }
      else
         okToDoAction = true;   
      
      if (okToDoAction)
         Platform.exit();
      */
      Platform.exit();
   }
   
}