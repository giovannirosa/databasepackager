package model;

import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;

public class UpdateModel {

	private int number;
	private String name;
	private boolean selected;
	private SVNDirEntry entry;
	private List<SVNDirEntry> childs;
	
	public UpdateModel(int number, String name, boolean selected,
			SVNDirEntry entry, List<SVNDirEntry> childs) {
		super();
		this.number = number;
		this.name = name;
		this.selected = selected;
		this.entry = entry;
		this.childs = childs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public SVNDirEntry getEntry() {
		return entry;
	}

	public void setEntry(SVNDirEntry entry) {
		this.entry = entry;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<SVNDirEntry> getChilds() {
		return childs;
	}

	public void setChilds(List<SVNDirEntry> childs) {
		this.childs = childs;
	}
}
