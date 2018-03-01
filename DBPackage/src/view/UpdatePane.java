package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.UpdateModel;

public class UpdatePane extends HBox {

	private CheckBox checkBox = new CheckBox();
	private Label number = new Label();
	private HBox buttonPane = new HBox();
	
	private UpdateModel model;
	
	public UpdatePane() {
		this.setSpacing(10);
		this.setAlignment(Pos.CENTER_LEFT);
		this.setPadding(new Insets(10));
		this.getChildren().addAll(checkBox,number,buttonPane);
	}

	public void setSelected(boolean sel) {
		this.checkBox.setSelected(sel);
	}

	public int getNumber() {
		return Integer.valueOf(number.getText());
	}

	public void setNumber(int n) {
		this.number.setText(Integer.toString(n));
	}

	public void addButton(Button... b) {
		this.buttonPane.getChildren().addAll(b);
	}

	public UpdateModel getModel() {
		return model;
	}

	public void setModel(UpdateModel model) {
		this.model = model;
		checkBox.setOnAction(e -> this.model.setSelected(checkBox.isSelected()));
	}
}
