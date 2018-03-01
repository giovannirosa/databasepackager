package controller;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DBPackage extends Application {
	
	ViewControl control = new ViewControl();

	public static void main(String[] args) {
        launch(args);
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		URL arquivoFXML = getClass().getResource(
				"/view/DBPackage.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(arquivoFXML);
        fxmlLoader.setController(control);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        control.setStage(primaryStage);
        control.setActions();
        control.setText("https://jenova.smgtec.com/svn/repos2/Database/NGAI-RELEASES/009_NGAI2017.1/Baseline/FACTSINT");
        
        primaryStage.setScene(new Scene(fxmlLoader.getRoot()));
        primaryStage.setTitle("Database Packager");
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(600);
        primaryStage.getIcons().add(new Image(getClass().getResource("/compressor.png").toExternalForm()));
        primaryStage.show();
	}
}
