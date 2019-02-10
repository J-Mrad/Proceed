// (c) 2019 EasyFX
// This code is licensed under MIT license (see LICENSE.txt for details)

package View;

import java.net.URL;
import java.util.HashMap;

import Model.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class LayoutManager {

	private static HashMap<String, Parent> RootMap = new HashMap<String, Parent>();

	public static void Initialise() {
		addRoot("Login", "Login.fxml");
		addRoot("Register", "Register.fxml");
		addRoot("EmployeeManager", "EmployeeManager.fxml");
		addRoot("Client", "AddCustomer.fxml");
		addRoot("Employee", "Employee.fxml");
		addRoot("LoanManager", "LoanManager.fxml");
		addRoot("RegionalManager", "RegionalManager.fxml");
	}

	public static Parent getRoot(String Key) {
		return RootMap.get(Key);
	}

	private static void addRoot(String Key, String target) {
		if (RootMap.containsKey(Key)) {
			return;
		} else {
			try {
				RootMap.put(Key, FXMLLoader.load(new URL(Constants.MODEL_DIRECTORY_URL + target)));
				SceneManager.addScene(Key, RootMap.get(Key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Node lookUp(String Key, String ID) {
		return RootMap.get(Key).lookup("#" + ID);
	}

}
