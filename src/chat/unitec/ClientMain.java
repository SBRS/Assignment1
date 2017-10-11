package chat.unitec;
import java.net.URL;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientMain extends Application {
	private static String name;
	
    public void start(Stage primaryStage) throws Exception {

	    	TextInputDialog dialog = new TextInputDialog("Walter");
	    	dialog.setTitle("Nickname Input");
	    	dialog.setHeaderText("");
	    	dialog.setContentText("Please enter your nickname:");
	
	    	Optional<String> result = dialog.showAndWait();
	    	if (result.isPresent()) {
	    	    name = result.get();
	        	URL fxmlUrl = this.getClass().getClassLoader().getResource("ClientLayout.fxml");
	            Pane mainPane = FXMLLoader.<Pane>load(fxmlUrl);
	            primaryStage.setTitle("Chat Client " + name);
	            primaryStage.setScene(new Scene(mainPane));
	            primaryStage.show();
	    	}
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

	public static String getName()
	{
		return name;
	}    
    
}