package view.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import controller.DBPackage;
import javafx.scene.image.ImageView;

public class Factory {
	
	public static ImageView getIcon(String name, double size) {
		ImageView iv = new ImageView(DBPackage.class.getClassLoader().getResource(name).toExternalForm());
		iv.setPreserveRatio(true);
		iv.setFitHeight(size);
		iv.setFitWidth(size);
		return iv;
	}
	
	public static void deleteFolder(Path p) {
		try {
			Files.walk(p, FileVisitOption.FOLLOW_LINKS)
				 .sorted(Comparator.reverseOrder())
				 .map(Path::toFile)
				 .peek(System.out::println)
				 .forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			ZipEntry zipEntry = new ZipEntry(fileName);
	        zipOut.putNextEntry(zipEntry);
	        byte[] bytes = new byte[1024];
	        int length;
	        while ((length = fis.read(bytes)) >= 0) {
	            zipOut.write(bytes, 0, length);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
