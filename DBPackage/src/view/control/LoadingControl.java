package view.control;

import java.io.IOException;

import controller.DBPackage;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadingControl {
	private Stage stage = new Stage();
	
	@FXML private ProgressBar pBar;
	@FXML private ProgressIndicator pInd;
	
	public LoadingControl(Window owner) {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Loading.fxml"));
		loader.setController(this);
    	try {
    		stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void show(String title) {
		stage.setTitle(title);
		setProgress(-1);
    	stage.show();
	}
	
	public void hide() {
		stage.hide();
	}
	
	public void setTask(Task<?> t) {
		pBar.progressProperty().unbind();
		pBar.progressProperty().bind(t.progressProperty());
		pInd.progressProperty().unbind();
		pInd.progressProperty().bind(t.progressProperty());
	}
	
	public void setProgress(double p) {
		pBar.setProgress(p);
		pInd.setProgress(p);
	}
}
