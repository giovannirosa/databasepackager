package view.control;

import java.io.IOException;

import controller.DBPackage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class AboutControl {
	Stage stage = new Stage();
	
	@FXML private Button closeBut;

	public AboutControl(Window owner) {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("About.fxml"));
		loader.setController(this);
    	try {
    		stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	closeBut.setOnAction(e -> stage.hide());
	}
	
	public void show() {
		stage.show();
	}
}
