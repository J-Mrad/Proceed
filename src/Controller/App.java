// (c) 2019 EasyFX
// This code is licensed under MIT license (see LICENSE.txt for details)

package Controller;

import View.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
	public static Stage WINDOW;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage Window) throws Exception {
		App.WINDOW = Window;
		Initialiser.Initialise();
		setupWindow();
	}

	private void setupWindow() {
		SceneManager.ChangeScene("LoginScene");
		Initialiser.PostInitialise();
		EventManager.Initialise();		
		WINDOW.show();
	}

}