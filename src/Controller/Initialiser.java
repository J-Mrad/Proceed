package Controller;

import Model.Constants;
import View.ImageHandler;
import View.LayoutManager;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class Initialiser {

	public static void Initialise() {
		DatabaseHandler.Initialise("admin","admin");
		LayoutManager.Initialise();
		ImageHandler.Initialise();

		App.WINDOW.setResizable(false);
		InitialiseImages();
		InitialiseHotKeys();
	}

	public static void PostInitialise() {
		App.WINDOW.setTitle(Constants.APP_NAME);
		App.WINDOW.getIcons().add(ImageHandler.getImage("PR2"));
	}

	private static void InitialiseImages() {
		((ImageView) LayoutManager.lookUp("Login", "pr1ImageView")).setImage(ImageHandler.getImage("Proceed"));
	}

	private static void InitialiseHotKeys() {
		App.WINDOW.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ESCAPE)
				App.WINDOW.close();
		});
	}

	
}
