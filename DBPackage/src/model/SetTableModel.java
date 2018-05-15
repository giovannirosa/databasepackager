package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SetTableModel {
	
	private final StringProperty  filename   = new SimpleStringProperty();
	
	public SetTableModel() {}
	
	public SetTableModel(String n) {
		filename.set(n);
	}
	
	public StringProperty filenameProperty() {
		return filename;
	}

	public String getFilename() {
		return filename.get();
	}
	
	public void setFilename(String n) {
		filename.set(n);
	}
}
