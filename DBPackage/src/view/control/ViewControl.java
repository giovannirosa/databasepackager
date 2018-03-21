package view.control;

import java.io.File;
import java.io.IOException;

import org.apache.poi.UnsupportedFileFormatException;
import org.tmatesoft.svn.core.SVNAuthenticationException;

import controller.DBPackage;
import controller.PackageControl;
import controller.SettingsJson;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ViewControl {
	private Stage stage;
	
	@FXML private BorderPane mainPane;
	
    @FXML private TextField textField;
    @FXML private Button searchBut;
    @FXML private Button genBut;
    @FXML private Button expBut;
    @FXML private Button collBut;
    @FXML private Button exitBut;
    @FXML private MenuItem setMenuItem;
    @FXML private MenuItem aboutMenuItem;

    TableControl tControl = new TableControl();
    AuthControl aControl = new AuthControl(this);
    LoadingControl lControl = new LoadingControl(stage);
    AboutControl abControl = new AboutControl(stage);
    SettingsJson sJson = SettingsJson.getInstance();
    SettingsControl sControl = new SettingsControl(stage);
    
    public ViewControl(Stage stage) {
		FXMLLoader fxmlLoader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Main.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        setActions();
        setText(sJson.getDefUrl());
        
        this.stage = stage;
        Scene scene = new Scene(fxmlLoader.getRoot());
        
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());
        
        stage.setScene(scene);
        stage.setTitle("Hermesus");
        stage.setMinHeight(800);
        stage.setMinWidth(600);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResource("/network.png").toExternalForm()));
        stage.show();
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    private void rememeber() {
    	if (aControl.isRemSelected()) {
    		sJson.setUser(aControl.getUser());
    		sJson.setPass(aControl.getPass());
    		sJson.storeJson();
    	} else {
    		sJson.setUser("");
    		sJson.setPass("");
    		sJson.storeJson();
    	}
    }
    
    public void search() {
    	Task<Boolean> task = new Task<Boolean>() {
    	    @Override public Boolean call() {
    	        // do your operation in here
    	    	PackageControl.createModels(textField.getText(),
    	    			aControl.getUser(),aControl.getPass());
    	    	Platform.runLater(new Runnable() {
    	    		@Override
    	    		public void run() {
    	    			tControl.populateTable();
    	    			expBut.setDisable(false);
    	    			collBut.setDisable(false);
    	    			genBut.setDisable(false);
    	    		}
    	    	});
    	    	return true;
    	    }
    	};

    	task.setOnRunning((e) -> lControl.show("Searching from svn..."));
    	task.setOnSucceeded((e) -> {
    		lControl.hide();
    		rememeber();
    	});
    	task.setOnFailed((e) -> {
    		lControl.hide();
    		if (e.getSource().getException() instanceof UnsupportedFileFormatException) {
    			ViewControl.showMessage("Unsupported File Format", "The file format used for description is not supported!\n"
    					+ "Please use .txt or .doc!");
    		} else if (e.getSource().getException().getCause() instanceof SVNAuthenticationException) {
    			ViewControl.showMessage("Authentication Failed", "The user and password do not match!");
    		} else {
    			ViewControl.showMessage("Proccess Failed", e.getSource().getException().getMessage());
    		}
    		aControl.showAuthDialog();
    	});
    	new Thread(task).start();
    }

    public static void showMessage(String title, String content) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(null);
    	alert.setContentText(content);
    	Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    	stage.getIcons().add(
    			new Image(ViewControl.class.getResource("/compressor.png").toString()));
    	
    	alert.showAndWait();
    }

    public void setActions() {
    	mainPane.setCenter(tControl.getTable());
    	setMenuItem.setOnAction(e -> sControl.show());
    	aboutMenuItem.setOnAction(e -> abControl.show());
    	exitBut.setOnAction(e -> {
    		stage.close();
    	});
    	searchBut.setOnAction(e -> {
    		if (!PackageControl.validateURL(textField.getText()))
    			return;
    		if (!sJson.getUser().isEmpty()) {
    			aControl.setUser(sJson.getUser());
    			aControl.setPass(sJson.getPass());
    			aControl.remBox.setSelected(true);
    		} else {
    			aControl.setUser("");
    			aControl.setPass("");
    			aControl.remBox.setSelected(false);
    		}
    		aControl.showAuthDialog();
    	});
    	expBut.setOnAction(e -> tControl.expandAll());
    	expBut.setDisable(true);
    	collBut.setOnAction(e -> tControl.collapseAll());
    	collBut.setDisable(true);
    	genBut.setOnAction(e -> {
    		FileChooser chooser = new FileChooser();
    		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Compressed", ".zip"));
    		chooser.setInitialDirectory(sJson.getDefSave().toFile()); 
    		File target = chooser.showSaveDialog(stage);
    		if (target!=null) {
    			Task<Boolean> task = new Task<Boolean>() {
    	    	    @Override public Boolean call() {
    	    	        // do your operation in here
    	    	    	PackageControl.createZip(target);
    	    	    	return true;
    	    	    }
    	    	};

    	    	task.setOnRunning((e1) -> lControl.show("Exporting from svn..."));
    	    	task.setOnSucceeded((e1) -> lControl.hide());
    	    	task.setOnFailed((e1) -> lControl.hide());
    	    	new Thread(task).start();
    		}
    	});
    	genBut.setDisable(true);
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }
}