package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

public class SettingsJson {
	
	private Path setPath;
	private String defUrl = "https://jenova.smgtec.com/svn/repos2/Database/NGAI-RELEASES/009_NGAI2017.1/Baseline/FACTSINT";
	private String descFiles = "RELEASE_NOTES.txt,ChangeLog.doc";
	private Path defSave = Paths.get(System.getProperty("user.home"));
	private Path criptoPath = Paths.get(System.getProperty("user.home")).resolve("wrap.exe");
	private String extFiles = "";
	private String user = "";
	private String pass = "";
	
	private static SettingsJson instance = null;
	
	public static SettingsJson getInstance() {
		if(instance == null) {
	         instance = new SettingsJson();
	      }
	      return instance;
	}
	
	private SettingsJson() {
		setPath = Paths.get(System.getProperty("user.home")).resolve(".hermesus");
		if (Files.notExists(setPath)) {
			try {
				Files.createDirectory(setPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		setPath = setPath.resolve("settings.txt");
		if (Files.notExists(setPath)) {
			try {
				Files.createFile(setPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			storeJson();
		} else {
			loadJson();
		}
	}
	
	public void loadJson() {
		byte content[] = null;;
		try {
			content = Files.readAllBytes(setPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject(new String(content));
		defUrl = json.getString("defUrl");
		descFiles = json.getString("descFiles");
		defSave = Paths.get(json.getString("defSave"));
		criptoPath = Paths.get(json.getString("criptoPath"));
		extFiles = json.getString("extFiles");
		user = json.getString("user");
		pass = json.getString("pass");
		
	}
	
	public void storeJson() {
		JSONObject json = new JSONObject();
		json.put("defUrl", defUrl);
		json.put("descFiles", descFiles);
		json.put("defSave", defSave);
		json.put("criptoPath", criptoPath);
		json.put("extFiles", extFiles);
		json.put("user", user);
		json.put("pass", pass);
		
		try {
			Files.write(setPath, json.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setFields(String url, String desc, String save, String cripto, String ext) {
		defUrl = url;
		descFiles = desc;
		defSave = Paths.get(save);
		criptoPath = Paths.get(cripto);
		extFiles = ext;
	}

	public Path getSetPath() {
		return setPath;
	}

	public void setSetPath(Path setPath) {
		this.setPath = setPath;
	}

	public String getDefUrl() {
		return defUrl;
	}

	public void setDefUrl(String defUrl) {
		this.defUrl = defUrl;
	}

	public String getDescFiles() {
		return descFiles;
	}

	public void setDescFiles(String descFiles) {
		this.descFiles = descFiles;
	}

	public Path getDefSave() {
		return defSave;
	}

	public void setDefSave(Path defSave) {
		this.defSave = defSave;
	}

	public Path getCriptoPath() {
		return criptoPath;
	}

	public void setCriptoPath(Path criptoPath) {
		this.criptoPath = criptoPath;
	}

	public String getExtFiles() {
		return extFiles;
	}

	public void setExtFiles(String extFiles) {
		this.extFiles = extFiles;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}
