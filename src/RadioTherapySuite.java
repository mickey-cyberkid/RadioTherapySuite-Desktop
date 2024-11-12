package radiotherapysuite;


import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;


/**
 *
 * @author OverComer
 */
public class RadioTherapySuite extends Application{
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
            Parent root = FXMLLoader.load(getClass().getResource("LoadPage.fxml"));
            Image icon = new Image(getClass().getResourceAsStream("images/basicmedia.jpg"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Treatment Time Calculator");
            primaryStage.getIcons().add(icon);
            // set icon later
            primaryStage.setScene(scene);
            
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.setResizable(true);
            primaryStage.show();
    }
    public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }
    
}
