package controller;

import java.io.File;
import java.util.List;
import org.tmatesoft.svn.core.SVNDirEntry;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.UpdatePane;

public class ViewControl {
	private Stage stage;
	
	@FXML private ScrollPane scrollPane;
	@FXML private VBox vPane;
	
    @FXML private TextField textField;
    @FXML private Button searchBut;
    @FXML private Button genBut;
    @FXML private Button exitBut;
    @FXML private MenuItem aboutMenuItem;

    AuthControl aControl = new AuthControl(this);
    LoadingControl lControl = new LoadingControl(stage);
    AboutControl abControl = new AboutControl(stage);
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    public void search() {
    	Task<Boolean> task = new Task<Boolean>() {
    	    @Override public Boolean call() {
    	        // do your operation in here
    	    	PackageControl.createModels(textField.getText(),
    	    			aControl.getUserTxt().getText(),aControl.getPassTxt().getText());
    	    	Platform.runLater(new Runnable() {
    	    		@Override
    	    		public void run() {
    	    			initGrid();
    	    			genBut.setDisable(false);
    	    		}
    	    	});  
    	    	return true;
    	    }
    	};

    	task.setOnRunning((e) -> lControl.show("Searching from svn..."));
    	task.setOnSucceeded((e) -> lControl.hide());
    	task.setOnFailed((e) -> lControl.hide());
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
    	aboutMenuItem.setOnAction(e -> abControl.show());
    	exitBut.setOnAction(e -> {
    		stage.close();
    	});
    	searchBut.setOnAction(e -> {
    		if (!PackageControl.validateURL(textField.getText()))
    			return;
    		aControl.showAuthDialog();
    	});
    	genBut.setOnAction(e -> {
    		FileChooser chooser = new FileChooser();
    		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Compressed", ".zip"));
    		chooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
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
    
    private void createButtons(List<SVNDirEntry> childs, UpdatePane uPane) {
    	childs.forEach(c -> {
    		Button but = null;
    		switch (c.getName()) {
			case "RELEASE_NOTES.txt":
				but = new Button();
				but.setText("Release Notes");
				break;
			case "ChangeLog.doc":
				but = new Button();
				but.setText("Change Log");
				break;
			default:
				break;
    		}
    		if (but!=null) {
    			but.setOnAction(e -> {
    				PackageControl.exportOpenFromSvn(c);
    			});
    			uPane.addButton(but);
    		}
    	});
    }

    public void initGrid() {
    	vPane.getChildren().clear();
    	PackageControl.getMap().forEach((k,v) -> {
    		UpdatePane uPane = new UpdatePane();
    		uPane.setNumber(k);
    		uPane.setSelected(true);
    		uPane.setModel(v);
    		createButtons(v.getChilds(),uPane);
    		vPane.getChildren().add(uPane);
    	});
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