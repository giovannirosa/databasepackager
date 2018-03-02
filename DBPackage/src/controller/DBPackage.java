package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.control.ViewControl;

public class DBPackage extends Application {

	public static void main(String[] args) {
        launch(args);
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new ViewControl(primaryStage);
	}
}
