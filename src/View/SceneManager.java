package View;

import java.util.HashMap;
import java.util.Map;

import Controller.App;
import Model.Constants;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

public abstract class SceneManager {

	private static Map<String, Scene> SceneMap = new HashMap<>();

	public static void addScene(String Key, Parent root) {
		if (SceneMap.containsKey(Key + "Scene"))
			return;

		((AnchorPane) root).setBackground(new Background(new BackgroundFill(Constants.BACKGROUND_COLOR, null, null)));

		SceneMap.put(Key + "Scene", new Scene(root));
	}

	public static Scene getScene(String Key) {
		return SceneMap.get(Key);
	}

	public static void ChangeScene(String Key) {
		App.WINDOW.setScene(getScene(Key));
		App.WINDOW.sizeToScene();
	}

}
