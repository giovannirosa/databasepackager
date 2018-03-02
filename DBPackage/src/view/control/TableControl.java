package view.control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;

import controller.PackageControl;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import model.TableModel;

public class TableControl {
	
	@FXML private TableView<TableModel> table;
	final ObservableList<TableModel> items = FXCollections.observableArrayList();

	public TableView<TableModel> getTable() {
		return table;
	}

	@SuppressWarnings("unchecked")
	public TableControl() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Table.fxml"));
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObservableList<TableColumn<TableModel, ?>> cols = table.getColumns();
		TableColumn< TableModel, Boolean > loadedColumn = (TableColumn<TableModel, Boolean>) cols.get(0);
		loadedColumn.setCellValueFactory(
				new Callback<CellDataFeatures<TableModel,Boolean>,ObservableValue<Boolean>>(){
					@Override public
					ObservableValue<Boolean> call( CellDataFeatures<TableModel,Boolean> p ){
						return p.getValue().getSelected(); }});
		loadedColumn.setCellFactory(
				new Callback<TableColumn<TableModel,Boolean>,TableCell<TableModel,Boolean>>(){
					@Override public
					TableCell<TableModel,Boolean> call( TableColumn<TableModel,Boolean> p ){
						return new CheckBoxTableCell<>(); }});
		TableColumn< TableModel, LocalDateTime > dateCol = (TableColumn<TableModel, LocalDateTime>) cols.get(5);
		dateCol.setCellFactory(tc -> new TableCell<TableModel, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime date, boolean empty) {
		        super.updateItem(date, empty);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
		        }
		    }
		});

		configCol(cols.get(0),25,25,false); // sel
		configCol(cols.get(1),50,70,true); // number
		configCol(cols.get(2).getColumns().get(0),100,800,true); // tasks
		configCol(cols.get(2).getColumns().get(1),50,100,true); // file
		configCol(cols.get(3),45,200,true); // rev
		configCol(cols.get(4),110,200,true); // aut
		configCol(cols.get(5),80,400,true); // date
		
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.requestLayout();
		table.setItems(items);
	}
	
	private void configCol(TableColumn<TableModel,?> col, double min, double max, boolean resize) {
		col.setMinWidth(min);
		col.setMaxWidth(max);
		col.setResizable(resize);
	}
	
	public void populateTable() {
		PackageControl.getMap().forEach((k,v) -> {
			TableModel tModel = new TableModel(true,v.getNumber(),v.getTasks(),v.getRevision(),v.getAuthor(),v.getDate());
			
			items.add(tModel);
    	});
	}
	
//	 private void createButtons(List<SVNDirEntry> childs, UpdatePane uPane) {
//	    	childs.forEach(c -> {
//	    		Button but = null;
//	    		switch (c.getName()) {
//				case "RELEASE_NOTES.txt":
//					but = new Button();
//					but.setText("Release Notes");
//					break;
//				case "ChangeLog.doc":
//					but = new Button();
//					but.setText("Change Log");
//					break;
//				default:
//					break;
//	    		}
//	    		if (but!=null) {
//	    			but.setOnAction(e -> {
//	    				PackageControl.exportOpenFromSvn(c);
//	    			});
//	    			uPane.addButton(but);
//	    		}
//	    	});
//	    }
}
