package view.control;

import java.io.IOException;

import controller.DBPackage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import model.SetTableModel;

public class SetTableControl {
	
	@FXML private HBox pane;

	@FXML private TableView<SetTableModel> table;
	
	@FXML private Button addBut;
	@FXML private Button removeBut;

	public SetTableControl() {
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("SetTable.fxml"));
		loader.setController(this);
    	try {
    		loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	addBut.setOnAction(e -> {
    		table.getItems().add(new SetTableModel());
    		table.requestFocus();
    		table.scrollTo(table.getItems().size()-1);
    		table.getSelectionModel().selectLast();
    	});
    	removeBut.setOnAction(e -> table.getItems().remove(table.getSelectionModel().getSelectedItem()));
	}
	
	public String getContent() {
		StringBuilder c = new StringBuilder();
		table.getItems().forEach(i -> c.append(i.getFilename()+","));
		
		return c.deleteCharAt(c.lastIndexOf(",")).toString();
	}
	
	public void setContent(String cont) {
		table.getItems().clear();
		for (String c : cont.split(",")) {
			table.getItems().add(new SetTableModel(c));
		}
	}
	
	public HBox getPane() {
		return pane;
	}
}
