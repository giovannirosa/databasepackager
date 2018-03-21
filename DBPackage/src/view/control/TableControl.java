package view.control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import controller.DBPackage;
import controller.PackageControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import model.TableModel;

public class TableControl {
	
	@FXML private TreeTableView<TableModel> table;
	private final TreeItem<TableModel> root = new TreeItem<>();
	
	private static final PseudoClass LEAF = PseudoClass.getPseudoClass("leaf");

	public TreeTableView<TableModel> getTable() {
		return table;
	}

	@SuppressWarnings("unchecked")
	public TableControl() {
		FXMLLoader loader = new FXMLLoader(DBPackage.class.getClassLoader().getResource("Table.fxml"));
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObservableList<TreeTableColumn<TableModel, ?>> cols = table.getColumns();
		TreeTableColumn< TableModel, Boolean > loadedColumn = (TreeTableColumn<TableModel, Boolean>) cols.get(0);
		loadedColumn.setCellValueFactory(
				new Callback<CellDataFeatures<TableModel,Boolean>,ObservableValue<Boolean>>(){
					@Override public
					ObservableValue<Boolean> call( CellDataFeatures<TableModel,Boolean> p ){
						TableModel m = p.getValue().getValue();
						if (m==null)
							return null;
						else
							return m.getSelected(); 
					}});
		loadedColumn.setCellFactory(
				new Callback<TreeTableColumn<TableModel,Boolean>,TreeTableCell<TableModel,Boolean>>(){
					@Override public
					TreeTableCell<TableModel,Boolean> call( TreeTableColumn<TableModel,Boolean> p ){
						CheckBoxTreeTableCell<TableModel, Boolean> cell = new CheckBoxTreeTableCell<>();
						cell.getStyleClass().add("hide-non-leaf");
						return cell; 
					}});
		CheckBox selAll = new CheckBox();
		selAll.setSelected(true);
		selAll.setOnAction(e -> {
			root.getChildren().forEach(child -> {
				if (selAll.isSelected())
					child.getValue().setSelected(true);
				else
					child.getValue().setSelected(false);
			});
		});
		loadedColumn.setGraphic(selAll);
		TreeTableColumn< TableModel, LocalDateTime > dateCol = (TreeTableColumn<TableModel, LocalDateTime>) cols.get(5);
		dateCol.setCellFactory(tc -> new TreeTableCell<TableModel, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime date, boolean empty) {
				super.updateItem(date, empty);
				if (empty || date==null) {
		            setText(null);
		        } else {
		            setText(date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
		        }
		    }
		});
		TreeTableColumn< TableModel, Integer > updateCol = (TreeTableColumn<TableModel, Integer>) cols.get(1);
		updateCol.setCellFactory(tc -> new TreeTableCell<TableModel, Integer>() {
			@Override
			protected void updateItem(Integer up, boolean empty) {
				super.updateItem(up, empty);
				if (empty || up==null || up == 0) {
		            setText(null);
		        } else {
		            setText(up.toString());
		        }
		    }
		});
		TreeTableColumn< TableModel, Long > revCol = (TreeTableColumn<TableModel, Long>) cols.get(3);
		revCol.setCellFactory(tc -> new TreeTableCell<TableModel, Long>() {
			@Override
			protected void updateItem(Long rev, boolean empty) {
				super.updateItem(rev, empty);
				if (empty || rev==null || rev == 0) {
		            setText(null);
		        } else {
		            setText(rev.toString());
		        }
		    }
		});

		configCol(cols.get(0),50,50,false); // sel
		configCol(cols.get(1),50,50,false); // number
		configCol(cols.get(2),100,3000,true); // tasks
		configCol(cols.get(3),60,60,false); // rev
		configCol(cols.get(4),110,110,false); // aut
		configCol(cols.get(5),110,110,false); // date
		
		table.setRowFactory(view -> new TreeTableRow<TableModel>() {

		    {
		        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
		            pseudoClassStateChanged(LEAF, newValue);
		        };
		        treeItemProperty().addListener((observable, oldItem, newItem) -> {
		            if (oldItem != null) {
		                oldItem.leafProperty().removeListener(listener);
		                setPrefHeight(26);
		            }
		            if (newItem != null) {
		                newItem.leafProperty().addListener(listener);
		                listener.changed(null, null, newItem.isLeaf());
		                String desc = newItem.getValue().getDesc();
		                if (desc!=null && !desc.equals("")) {
		                	int x = desc.split("\n").length;
		                	setPrefHeight(26*x);
		                }
		            } else {
		                listener.changed(null, null, Boolean.FALSE);
		            }
		        });
		    }

		});
		
		table.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		table.requestLayout();
		root.setExpanded(true);
		table.setRoot(root);
		table.setShowRoot(false);
	}
	
	public void expandAll() {
		root.getChildren().forEach(child -> child.setExpanded(true));
	}
	
	public void collapseAll() {
		root.getChildren().forEach(child -> child.setExpanded(false));
	}
	
	private void configCol(TreeTableColumn<TableModel,?> col, double min, double max, boolean resize) {
		col.setMinWidth(min);
		col.setMaxWidth(max);
		col.setResizable(resize);
	}
	
	public void populateTable() {
		PackageControl.getMap().forEach((k,v) -> {
			String[] msg = v.getTasks().split("\n");
			String d = msg.length>1 ? msg[0]+" [...]":msg[0];
			TreeItem<TableModel> item = new TreeItem<>(new TableModel(true,v.getNumber(),d,v.getRevision(),v.getAuthor(),v.getDate()));
			TreeItem<TableModel> desc = new TreeItem<>(new TableModel(v.getTasks()));
			item.getChildren().add(desc);
			item.expandedProperty().addListener((observable, oldItem, newItem) -> {
				if (!oldItem && newItem)
					item.getValue().setDesc("");
				if (oldItem && !newItem)
					item.getValue().setDesc(d);
			});
			root.getChildren().add(item);
    	});
	}
}
