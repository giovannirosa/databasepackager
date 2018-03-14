package view.control;

import java.io.IOException;
import java.util.Optional;

import controller.DBPackage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import view.util.Factory;

public class AuthControl {
	
	public static final ButtonType AUTH = new ButtonType("Authenticate");
	
	@FXML
    DialogPane loginPane;
    @FXML
    TextField userTxt;
    @FXML
    PasswordField passTxt;
    @FXML
    CheckBox remBox;
    @FXML
    ButtonType authBut;
    @FXML
    ButtonType cancelBut;
    
    ViewControl vControl;
    
    Dialog<ButtonType> dialog;
    
    public AuthControl(ViewControl v) {
    	vControl = v;
    	dialog = new Dialog<>();
    	dialog.setTitle("Authentication");
    	FXMLLoader dialogLoader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Auth.fxml"));
    	dialogLoader.setController(this);
    	try {
			dialog.setDialogPane(dialogLoader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	dialog.getDialogPane().setHeaderText("Please enter valid svn user and password:");
    	Button cBut = (Button) dialog.getDialogPane().lookupButton(cancelBut);
    	cBut.setGraphic(Factory.getIcon("close.png", 20));
    	cBut.translateXProperty().bind(cBut.prefWidthProperty().divide(-1.05));
    	Button authBut = (Button) dialog.getDialogPane().lookupButton(getLoginButton());
    	authBut.setGraphic(Factory.getIcon("auth.png", 20));
    	authBut.sceneProperty().addListener((observable, oldValue, newScene) -> {
    	    if (newScene != null) {
    	        newScene.getAccelerators().put(
    	        		new KeyCodeCombination(KeyCode.ENTER),
    	                authBut::fire
    	        );
    	    }
    	});
    	Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    	stage.getIcons().add(
    			new Image(this.getClass().getResource("/auth.png").toString()));
    }
    
    public void showAuthDialog(boolean clear) {
    	if (clear) {
    		userTxt.clear();
    		passTxt.clear();
    	}
    	Optional<ButtonType> op = dialog.showAndWait();
    	op.filter(AuthControl.AUTH::equals)
    		.ifPresent(button -> vControl.search());
    }
    
	public DialogPane getLoginPane() {
		return loginPane;
	}
	public void setLoginPane(DialogPane loginPane) {
		this.loginPane = loginPane;
	}
	public TextField getUserTxt() {
		return userTxt;
	}
	public void setUserTxt(TextField userTxt) {
		this.userTxt = userTxt;
	}
	public PasswordField getPassTxt() {
		return passTxt;
	}
	public void setPassTxt(PasswordField passTxt) {
		this.passTxt = passTxt;
	}
	public ButtonType getLoginButton() {
		return authBut;
	}
	public void setLoginButton(ButtonType loginButton) {
		this.authBut = loginButton;
	}
}
