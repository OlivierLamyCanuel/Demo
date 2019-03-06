

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import controller.ParseurController;


/**
 * Création de l'interface utilisateur à partir du fichier "parseurView.fxml"
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 * 
 */
public class ParseurView extends Application {
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("/controller/parseurView.fxml"));
		Scene scene = new Scene(root, 1000, 800);
		
		ParseurController controller = new ParseurController();	
		
		primaryStage.setTitle("UML PARSEUR");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	/**
	 * Démarre l'application.
	 * @param args
	 */
	public static void main(String[] args) {
		 launch(args);
	}
}
