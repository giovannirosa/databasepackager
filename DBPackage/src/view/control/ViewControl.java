package view.control;

import java.io.File;
import java.io.IOException;
import controller.DBPackage;
import controller.PackageControl;
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
    @FXML private MenuItem aboutMenuItem;

    TableControl tControl = new TableControl();
    AuthControl aControl = new AuthControl(this);
    LoadingControl lControl = new LoadingControl(stage);
    AboutControl abControl = new AboutControl(stage);
    
    public ViewControl(Stage stage) {
		FXMLLoader fxmlLoader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("DBPackage.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        setActions();
        setText("https://jenova.smgtec.com/svn/repos2/Database/NGAI-RELEASES/009_NGAI2017.1/Baseline/FACTSINT");
        
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
    
    public void search() {
    	Task<Boolean> task = new Task<Boolean>() {
    	    @Override public Boolean call() {
    	        // do your operation in here
    	    	PackageControl.createModels(textField.getText(),
    	    			aControl.getUserTxt().getText(),aControl.getPassTxt().getText());
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
    	task.setOnSucceeded((e) -> lControl.hide());
    	task.setOnFailed((e) -> {
    		lControl.hide();
    		ViewControl.showMessage("Authentication Failed", "Authentication failed!");
    		aControl.showAuthDialog(false);
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
    	aboutMenuItem.setOnAction(e -> abControl.show());
    	exitBut.setOnAction(e -> {
    		stage.close();
    	});
    	searchBut.setOnAction(e -> {
    		if (!PackageControl.validateURL(textField.getText()))
    			return;
    		aControl.showAuthDialog(true);
    	});
    	expBut.setOnAction(e -> tControl.expandAll());
    	expBut.setDisable(true);
    	collBut.setOnAction(e -> tControl.collapseAll());
    	collBut.setDisable(true);
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