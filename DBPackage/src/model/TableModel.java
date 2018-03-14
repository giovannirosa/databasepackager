package model;

import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {
	
	private final BooleanProperty selected = new SimpleBooleanProperty();
	private final IntegerProperty  number   = new SimpleIntegerProperty();
	private final StringProperty  desc    = new SimpleStringProperty();
	private final LongProperty  revision = new SimpleLongProperty();
	private final StringProperty  author   = new SimpleStringProperty();
	private final ObjectProperty<LocalDateTime>  date = new SimpleObjectProperty<>();
	
	public TableModel(boolean s, int n, String de, long r, String a, LocalDateTime d) {
		selected.set(s);
		number.set(n);
		desc.set(de);
		revision.set(r);
		author.set(a);
		date.set(d);
	}
	
	public TableModel(String d) {
		desc.set(d);
	}
	
	public BooleanProperty getSelected() {
		return selected;
	}
	
	public void setSelected(boolean s) {
		selected.set(s);
	}
	
	public int getNumber() {
		return number.get();
	}
	
	public void setNumber(int n) {
		number.set(n);
	}
	
	public String getDesc() {
		return desc.get();
	}
	
	public void setDesc(String t) {
		desc.set(t);
	}
	
	public long getRevision() {
		return revision.get();
	}
	
	public void setRevision(long t) {
		revision.set(t);
	}
	
	public String getAuthor() {
		return author.get();
	}
	
	public void setAuthor(String t) {
		author.set(t);
	}
	
	public LocalDateTime getDate() {
		return date.get();
	}
	
	public void setDate(LocalDateTime t) {
		date.set(t);
	}
}
