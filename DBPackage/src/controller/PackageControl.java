package controller;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import model.UpdateModel;
import view.control.ViewControl;
import view.util.Factory;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
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
	
	private static SVNRepository repository;
	private static SVNClientManager manager;
	private static File tempDir;
	private static SettingsJson sJson = SettingsJson.getInstance();
	
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
	
	private static boolean testExternals(SVNDirEntry e) {
		if (!(e.getKind()==SVNNodeKind.FILE))
			return false;
		
		String n = e.getName();
		String ext[] = sJson.getExtFiles().split(",");
		for (String x : ext) {
			if (n.equals(x))
				return true;
		}

		return false;
	}

	public static void listEntries() throws SVNException {
		try (Stream<SVNDirEntry> eStream = entriesStream("")) {
			eStream.filter(f -> testEntries(f))
				   .forEach(f -> collectModels(f));
		}
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
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
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
						Files.list(d).forEach(f -> {
							try {
								Files.delete(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
					doExport(v.getEntry(), d.toFile());//TODO finalizar wrapper
					Files.list(d).filter(p -> p.getFileName().toString().endsWith("BODY.sql")).forEach(p -> {
						runWrap(p.toAbsolutePath().toString(),isWindows);
						try {
							Files.delete(p);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		try (Stream<SVNDirEntry> eStream = entriesStream("")) {
			eStream.filter(f -> testExternals(f))
				   .forEach(f -> doExport(f, tempDir.toPath().resolve(f.getName()).toFile()));
		}
		try (FileOutputStream fos = new FileOutputStream(target);
				ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			String filename = target.toPath().getFileName().toString();
			filename = filename.substring(0, filename.indexOf("."));
			Factory.zipFile(tempDir, filename, zipOut);
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
			Path p = local.resolve(file);
			if (p.toFile().exists())
				s = Files.lines(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private static String extractDoc(Path local, String file) {
		String s = "";
		try (FileInputStream fis = new FileInputStream(local.resolve(file).toFile());
				HWPFDocument doc = new HWPFDocument(fis);) {
			
			Range range = doc.getRange();
		    TableIterator itr = new TableIterator(range);
		    while(itr.hasNext()){
		        Table table = itr.next();
		        TableRow row = table.getRow(1);
		        TableCell cell = row.getCell(1);
		        String qc = cell.getParagraph(0).text();
		        if (qc.length() > 5)
		        	qc = qc.substring(0, 5) +"/"+ qc.substring(5);
		        cell = row.getCell(5);
		        s = "QC"+qc+" - "+cell.getParagraph(0).text();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private static String combineLines(String l1, String l2) {
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
	}
	
	private static void collectModels(SVNDirEntry e) {
		String n = e.getName();
		int i = Integer.valueOf(n.substring(n.length()-3, n.length()));
		
		List<SVNDirEntry> childs;
		try (Stream<SVNDirEntry> eStream = entriesStream(e.getName())) {
			childs = eStream.collect(Collectors.toList());
		}
		String files[] = sJson.getDescFiles().split(",");
		
		Path local = tempDir.toPath().resolve(Paths.get(e.getName()));
		Path descFile = null;
		String desc = "";
		
		for (String f : files) {
			Optional<SVNDirEntry> opt = childs.parallelStream().filter(a -> a.getName().equals(f)).findFirst();
			opt.ifPresent(g -> exportOpenFromSvn(g,false));
			if (!opt.isPresent())
				continue;

			String ext = f.substring(f.indexOf("."));
			if (ext.equalsIgnoreCase(".txt")) {
				try (Stream<String> lines = linesStream(local, f)) {
					if (lines!=null)
						desc = lines.filter(l -> testLine(l,e.getName()))
									.reduce("", (l1,l2) -> combineLines(l1,l2));
				}
				descFile = local.resolve(f);
				break;
			} else if (ext.equalsIgnoreCase(".doc")) {
				desc = extractDoc(local, "ChangeLog.doc");
				descFile = local.resolve(f);
				break;
			}
		}

		UpdateModel u = new UpdateModel(i,n,true,e.getRevision(),desc,e.getAuthor(),
				LocalDateTime.ofInstant(e.getDate().toInstant(), ZoneId.systemDefault()),e,childs,local,descFile);
		map.put(i, u);
	}
	
	private static int runWrap(String iname, boolean isWindows) {
		ProcessBuilder builder = new ProcessBuilder();
		String oname = iname.replace(".sql", ".plb");
		if (isWindows) {
		    builder.command(sJson.getCriptoPath().resolve("wrap.exe").toString(), "iname='"+iname+"' oname='"+oname+"'");
		} else {
		    builder.command(sJson.getCriptoPath().resolve("wrap").toString(), "iname='"+iname+"' oname='"+oname+"'");
		}
		int exitCode = -1;
		StringBuilder msg = new StringBuilder();;
		Consumer<String> consumer = (s) -> msg.append(s);
		try {
			Process process = builder.start();
			StreamGobbler streamGobbler = 
					  new StreamGobbler(process.getInputStream(), consumer);
					Executors.newSingleThreadExecutor().submit(streamGobbler);
					exitCode = process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if (exitCode != 0)
			throw new IllegalStateException(msg.toString());
		return exitCode;
	}
	
	private static class StreamGobbler implements Runnable {
	    private InputStream inputStream;
	    private Consumer<String> consumer;
	 
	    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
	        this.inputStream = inputStream;
	        this.consumer = consumer;
	    }
	 
	    @Override
	    public void run() {
	        new BufferedReader(new InputStreamReader(inputStream)).lines()
	          .forEach(consumer);
	    }
	}
}
