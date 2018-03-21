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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SettingsControl {
	Stage stage = new Stage();
	
	@FXML private TextField urlTxt;
	@FXML private TextField descTxt;
	@FXML private TextField saveTxt;
	@FXML private TextField criptoTxt;
	@FXML private TextField extTxt;
	
	@FXML private Button searchSaveBut;
	@FXML private Button searchCriptoBut;
	@FXML private Button saveBut;
	@FXML private Button cancelBut;
	
	SettingsJson sJson = SettingsJson.getInstance();
	
	public SettingsControl(Window owner) {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Settings.fxml"));
		loader.setController(this);
    	try {
    		stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
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
    		sJson.setFields(urlTxt.getText(), descTxt.getText(), saveTxt.getText(), criptoTxt.getText(), extTxt.getText());
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
    		FileChooser chooser = new FileChooser();
    		chooser.setInitialDirectory(Paths.get(criptoTxt.getText().substring(0, criptoTxt.getText().lastIndexOf("\\"))).toFile()); 
    		chooser.setInitialFileName(criptoTxt.getText().substring(criptoTxt.getText().lastIndexOf("\\")+1));
    		File target = chooser.showOpenDialog(stage);
    		if (target!=null) {
    			criptoTxt.setText(target.getAbsolutePath());
    		}
    	});
	}
	
	private boolean validateDescFiles() {
		String desc[] = descTxt.getText().split(",");
		for (String d : desc) {
			String ext = d.substring(d.indexOf("."));
			if (!ext.equalsIgnoreCase(".txt") && !ext.equalsIgnoreCase(".doc")) {
				ViewControl.showMessage("Invalid Description Files Extension", "The extension '"+ext+"' for description file '"+d+"' is not supported! "
						+ "Please use '.txt' or '.doc'!");
				return false;
			}
		}
		return true;
	}
	
	private void resumeFields() {
		urlTxt.setText(sJson.getDefUrl());
		descTxt.setText(sJson.getDescFiles());
		saveTxt.setText(sJson.getDefSave().toString());
		criptoTxt.setText(sJson.getCriptoPath().toString());
		extTxt.setText(sJson.getExtFiles());
	}
	
	public void show() {
		stage.show();
	}

}
