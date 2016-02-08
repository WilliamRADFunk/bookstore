/*
 * Name: William Funk 0969985
 * Course: CNT 4714 – Spring 2016
 * Assignment title: Program 1 – Event-driven Programming
 * Date: Sunday January 24, 2016
*/
package checkout;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("checkout.fxml"));
        primaryStage.setTitle("Funky Town Books");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
