// (c) 2019 EasyFX
// This code is licensed under MIT license (see LICENSE.txt for details)

package View;

import java.util.HashMap;
import java.util.Map;

import Model.Constants;
import javafx.scene.image.Image;

public abstract class ImageHandler {

	private static Map<String, Image> ImgMap = new HashMap<String, Image>();

	
	public static void Initialise() {
		addImage("PR1", "PR1.png");
		addImage("PR2", "PR2.png");
		addImage("Proceed", "Proceed.png");
	}
	
	
	private static void addImage(String Key, String Image) {

		if (ImgMap.containsKey(Key))
			return;

		ImgMap.put(Key, new Image(Constants.MODEL_DIRECTORY_URL + Image));

	}

	public static Image getImage(String Key) {
		return ImgMap.get(Key);
	}

}
