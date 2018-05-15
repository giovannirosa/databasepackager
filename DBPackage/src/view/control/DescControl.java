package view.control;

import java.io.IOException;

import controller.DBPackage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DescControl {
	Stage stage = new Stage();
	
	@FXML private Label upLabel;
	@FXML private TextArea txtArea;
	@FXML private Button closeBut;
	
	public DescControl(Window owner) {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		stage.setAlwaysOnTop(true);
		
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Description.fxml"));
		loader.setController(this);
    	try {
    		stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	closeBut.setOnAction(e -> stage.hide());
	}
	
	public void setUpdate(int n) {
		upLabel.setText("Update "+n);
	}
	
	public void setDesc(String t) {
		txtArea.setText(t);
	}

	public void show() {
		stage.show();
	}
}
