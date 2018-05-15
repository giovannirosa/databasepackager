package view.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import controller.DBPackage;
import controller.PackageControl;
import controller.SettingsJson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SettingsControl {
	Stage stage = new Stage();
	
	@FXML private GridPane gridPane;
	
	@FXML private TextField urlTxt;
	@FXML private TextField saveTxt;
	@FXML private TextField criptoTxt;
	
	@FXML private Button searchSaveBut;
	@FXML private Button searchCriptoBut;
	@FXML private Button saveBut;
	@FXML private Button cancelBut;
	
	SetTableControl dTableControl = new SetTableControl();
	SetTableControl eTableControl = new SetTableControl();
	
	SettingsJson sJson = SettingsJson.getInstance();
	
	public SettingsControl(Window owner) {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(false);
		stage.setTitle("Settings");
		stage.getIcons().add(new Image(getClass().getResource("/settings.png").toExternalForm()));
		
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Settings.fxml"));
		loader.setController(this);
    	try {
    		stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	gridPane.add(dTableControl.getPane(), 1, 3);
    	gridPane.add(eTableControl.getPane(), 1, 4);
    	
    	resumeFields();
    	cancelBut.setOnAction(e -> {
    		stage.hide();
    		resumeFields();
    	});
    	saveBut.setOnAction(e -> {
    		if (!PackageControl.validateURL(urlTxt.getText()))
    			return;
    		if (!validateDescFiles())
    			return;
    		sJson.setFields(urlTxt.getText(), dTableControl.getContent(), saveTxt.getText(), criptoTxt.getText(), eTableControl.getContent());
    		sJson.storeJson();
    		stage.hide();
    	});
    	searchSaveBut.setOnAction(e -> {
    		DirectoryChooser chooser = new DirectoryChooser();
    		chooser.setInitialDirectory(new File(saveTxt.getText())); 
    		File target = chooser.showDialog(stage);
    		if (target!=null) {
    			saveTxt.setText(target.getAbsolutePath());
    		}
    	});
    	searchCriptoBut.setOnAction(e -> {
    		DirectoryChooser chooser = new DirectoryChooser();
    		chooser.setInitialDirectory(Paths.get(criptoTxt.getText()).toFile()); 
    		File target = chooser.showDialog(stage);
    		if (target!=null) {
    			criptoTxt.setText(target.getAbsolutePath());
    		}
    	});
	}
	
	private boolean validateDescFiles() {
		String desc[] = dTableControl.getContent().split(",");
		for (String d : desc) {
			int i = d.indexOf(".");
			String ext = "";
			if (i > 0)
				ext = d.substring(d.indexOf("."));
			if (ext.isEmpty() && !ext.equalsIgnoreCase(".txt") && !ext.equalsIgnoreCase(".doc")) {
				ViewControl.showMessage("Invalid Description Files Extension", "The extension '"+ext+"' for description file '"+d+"' is not supported! "
						+ "Please use '.txt' or '.doc'!");
				return false;
			}
		}
		return true;
	}
	
	public void resumeFields() {
		urlTxt.setText(sJson.getDefUrl());
		dTableControl.setContent(sJson.getDescFiles());
		saveTxt.setText(sJson.getDefSave().toString());
		criptoTxt.setText(sJson.getCriptoPath().toString());
		eTableControl.setContent(sJson.getExtFiles());
	}
	
	public void show() {
		stage.show();
	}

}
