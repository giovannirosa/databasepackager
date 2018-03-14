package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import model.UpdateModel;
import view.control.ViewControl;
import view.util.Factory;

import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import javafx.application.Platform;

public class PackageControl {
	static private Map<Integer,UpdateModel> map = new TreeMap<>();
	
	static SVNRepository repository;
	static SVNClientManager manager;
	static File tempDir;
	
	public void checkOut(SVNDirEntry e) {
		final SvnCheckout checkout = manager.getUpdateClient().getOperationsFactory().createCheckout();
		try {
		    checkout.setSingleTarget(SvnTarget.fromFile(
		    		Files.createDirectory(
		    				Paths.get(tempDir.getPath(),e.getName())).toFile()));
		    checkout.setSource(SvnTarget.fromURL(e.getURL()));
		    
		    checkout.run();
		} catch (IOException | SVNException e1) {
			e1.printStackTrace();
		}
	}
	
	public static boolean validateURL(String url) {
		if (!url.startsWith("https://jenova.smgtec.com/svn/repos2/Database/NGAI-RELEASES")) {
			ViewControl.showMessage("Invalid URL", "URL should starts with: 'https://jenova.smgtec.com/svn/repos2/Database/NGAI-RELEASES'");
			return false;
		}
		if (!url.endsWith("FACTSINT") && !url.endsWith("FACTSINT/")) {
			ViewControl.showMessage("Invalid URL", "URL should ends with: 'FACTSINT' or 'FACTSINT/'");
			return false;
		}
		return true;
	}
	
	public static void createModels(String url, String user, String pass) {
		map.clear();
		DAVRepositoryFactory.setup();
		try {
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, pass.toCharArray());
			SVNURL test = SVNURL.parseURIEncoded(url);
			
			repository = SVNRepositoryFactory.create(test, null);
			repository.setAuthenticationManager(authManager);
			repository.testConnection();
			
			manager = SVNClientManager.newInstance(
					SVNWCUtil.createDefaultOptions(true),authManager);
			
			tempDir = Files.createTempDirectory("DBPackage").toFile();
			Runtime.getRuntime().addShutdownHook(new Thread(() -> Factory.deleteFolder(tempDir.toPath())));
		    System.out.println(tempDir.toPath().getFileName().toString()+" created!");
			
			System.out.println( "Repository Root: " + repository.getRepositoryRoot( true ) );
			System.out.println(  "Repository UUID: " + repository.getRepositoryUUID( true ) );

			SVNNodeKind nodeKind = repository.checkPath( "" ,  -1 );
			if ( nodeKind == SVNNodeKind.NONE ) {
				System.err.println( "There is no entry at '" + url + "'." );
				System.exit( 1 );
			} else if ( nodeKind == SVNNodeKind.FILE ) {
				System.err.println( "The entry at '" + url + "' is a file while a directory was expected." );
				System.exit( 1 );
			}

			listEntries();

			System.out.println( "Repository latest revision: " + repository.getLatestRevision() );
		} catch (SVNAuthenticationException a) {
			throw new RuntimeException(a);
		} catch (SVNException | IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void doExport(SVNDirEntry e, File file) {
		SVNUpdateClient updateClient = manager.getUpdateClient();
		SVNURL url = e.getURL();
		//the revision number which should be looked upon for the file path
		SVNRevision pegRevision = SVNRevision.create(-1);
		//the revision number which is required to be exported.
		SVNRevision revision = SVNRevision.create(-1);
		//if there is any special character for end of line (in the file) then it is required. For our use case, //it can be null, assuming there are no special characters. In this case the OS specific EoF style will //be assumed
		String eolStyle = null;
		//this would force the operation 
		boolean force = true;
		//Till what extent under a directory, export is required, is determined by depth. INFINITY means the whole subtree of that directory will be exported
		SVNDepth recursive = SVNDepth.INFINITY;
		try {
			updateClient.doExport(url, file, pegRevision, revision, eolStyle, force, recursive);
		} catch (SVNException e1) {
			e1.printStackTrace();
		}
	}

	public static void exportOpenFromSvn(SVNDirEntry e, boolean open) {
		try {
			String u = e.getURL().removePathTail().toString();
			Path d = tempDir.toPath().resolve(
					Paths.get(u.substring(u.indexOf("Update"))).getFileName());
			if (Files.notExists(d))
				Files.createDirectory(d);
			Path f = d.resolve(e.getName());
			if (Files.notExists(f)) {
				Files.createFile(f);
				doExport(e, f.toFile());
			}
			if (open && Desktop.isDesktopSupported())
				Desktop.getDesktop().open(f.toFile());
		} catch (IOException | SVNException e1) {
			e1.printStackTrace();
		}
    }
	
	private static boolean testEntries(SVNDirEntry e) {
		if (!(e.getKind()==SVNNodeKind.DIR))
			return false;
		
		String n = e.getName();
		if (!n.startsWith("Update"))
			return false;
		
		int i = Integer.valueOf(n.substring(n.length()-3, n.length()));
		return i > 99;
	}

	public static void listEntries() throws SVNException {
		entriesStream("").filter(f -> testEntries(f))
						.forEach(f -> collectModels(f));
		map.forEach((k,v) -> System.out.println(v.getName()));
	}

	public static Map<Integer,UpdateModel> getMap() {
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private static Stream<SVNDirEntry> entriesStream(String path) {
		Collection<SVNDirEntry> entries = null;
		try {
			entries = repository.getDir( path, -1 , null , (Collection<SVNDirEntry>) null );
		} catch (SVNException e1) {
			e1.printStackTrace();
		}
		return entries.stream();
	}
	
	public static void createZip(File target) {
		map.forEach((k,v) -> {
			Path d = tempDir.toPath().resolve(v.getEntry().getName());
			if (!v.isSelected()) {
				if (Files.exists(d))
					Factory.deleteFolder(d);
			} else {
				try {
					if (Files.notExists(d)) {
						Files.createDirectory(d);
					} else {
						Factory.deleteFolder(d);
						Files.createDirectory(d);			
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				doExport(v.getEntry(), d.toFile());
			}
		});
		try (FileOutputStream fos = new FileOutputStream(target);
				ZipOutputStream zipOut = new ZipOutputStream(fos);) {
		        Factory.zipFile(tempDir, tempDir.getName(), zipOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			ViewControl.showMessage("Export Done", "Package successfully exported!");
    		}
    	});
	}
	
	private static boolean testLine(String l, String n) {
		if (l.equals("=========") || l.equals(n) || l.trim().equals(""))
			return false;
		return true;
	}
	
	private static Stream<String> linesStream(Path local, String file) {
		Stream<String> s = null;
		try {
			Path p = local.resolve(Paths.get(file));
			if (p.toFile().exists())
				s = Files.lines(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private static void collectModels(SVNDirEntry e) {
		String n = e.getName();
		int i = Integer.valueOf(n.substring(n.length()-3, n.length()));
		
		List<SVNDirEntry> childs = entriesStream(e.getName()).collect(Collectors.toList());
		childs.parallelStream().filter(f -> f.getName().equals("RELEASE_NOTES.txt")).findFirst().ifPresent(g -> exportOpenFromSvn(g,false));
		
		Path local = tempDir.toPath().resolve(Paths.get(e.getName()));
		Stream<String> lines = linesStream(local, "RELEASE_NOTES.txt");
		String desc = "";
		if (lines!=null)
			desc = lines.filter(l -> testLine(l,e.getName()))
						.reduce("", (l1,l2) -> {
							if (l2.length() > 180) {
								String[] a = l2.split("(?<=\\G.{180})");
								l2 = "";
								for (String h : a) {
									if (!l2.isEmpty())
										l2 = l2.concat("\n"+h);
									else
										l2 = l2.concat(h);
								}
							}
							String r = !l1.equals("") ? l1.concat("\n"+l2) : l2;
							return r.trim();
							});

		UpdateModel u = new UpdateModel(i,n,true,e.getRevision(),desc,e.getAuthor(),
				LocalDateTime.ofInstant(e.getDate().toInstant(), ZoneId.systemDefault()),e,childs,local);
		map.put(i, u);
	}
}
