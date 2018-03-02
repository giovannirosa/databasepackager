package model;

import java.time.LocalDateTime;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;

public class UpdateModel {

	private int number;
	private String name;
	private boolean selected;
	private long revision;
	private String tasks;
	private String author;
	private LocalDateTime date;
	private SVNDirEntry entry;
	private List<SVNDirEntry> childs;

	public UpdateModel(int number, String name, boolean selected, long revision, String tasks, String author,
			LocalDateTime date, SVNDirEntry entry, List<SVNDirEntry> childs) {
		super();
		this.number = number;
		this.name = name;
		this.selected = selected;
		this.revision = revision;
		this.tasks = tasks;
		this.author = author;
		this.date = date;
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

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getTasks() {
		return tasks;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
