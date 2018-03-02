package view.util;

import javafx.scene.image.ImageView;

public class Factory {
	
	public static ImageView getIcon(String name, double size) {
		ImageView iv = new ImageView(Factory.class.getResource("/"+name).toExternalForm());
		iv.setPreserveRatio(true);
		iv.setFitHeight(size);
		iv.setFitWidth(size);
		return iv;
	}

}
