package Controller;

import Model.Client;
import Model.Employee;
import Model.EmployeeManager;
import Model.LoanManager;
import Model.RegionalManager;
import View.LayoutManager;
import View.SceneManager;
import View.ShakeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class EventManager {

	public static void Initialise() {

		TextField user = (TextField) LayoutManager.lookUp("Login", "Username");
		PasswordField pass = (PasswordField) LayoutManager.lookUp("Login", "Password");
		
		LayoutManager.lookUp("Login", "LoginButton").addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			String Username = user.getText(),
					Password = pass.getText();
			int result = LoginHandler.CheckLogin(Username, Password);

			switch (result) {
			case -1: {
				//UserName or Password Incorrect
				new ShakeTransition(user).playFromStart();
				new ShakeTransition(pass).playFromStart();
				
				break;
			}
			case -2: {
				// Employee Manager
				DatabaseHandler.Initialise("employeeManager", "manager");
				EmployeeManager.Initialise();
				SceneManager.ChangeScene("EmployeeManagerScene");
				break;
			}
			case -3: {
				//Loan Manager
				LoanManager.Initialise();
				SceneManager.ChangeScene("LoanManagerScene");
				break;
			}
			case -4: {
				//Regional Manager
				RegionalManager.Initialise();
				SceneManager.ChangeScene("RegionalManagerScene");
				break;
			}
			default: {
				//Employee
				Employee.Initialise(result);
				SceneManager.ChangeScene("EmployeeScene");
				break;
			}

			}

		});
	}

	public static void setShowFiredEmployees(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;
			EmployeeManager.FillList();
		});
	}

	public static void addToEmployeeEditEvent(Node node, int ID) {
		node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				EmployeeManager.Edit(ID);
				EmployeeManager.setChosenEmployeeID(ID);
			}
		});
	}

	public static void setResetEmployeeManagerEvent(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_RELEASED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			EmployeeManager.Reset();
		});
	}

	public static void setAddEmployeeOrApplyChanges(Button button) {

		button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;
			if (button.getText().equals("Add Employee") && EmployeeManager.CheckForErrors())
				EmployeeManager.AddEmployee();
			else if (button.getText().equals("Apply Changes") && EmployeeManager.CheckForErrors())
				EmployeeManager.ApplyChanges();
		});

	}

	public static void setLogout(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			DatabaseHandler.Initialise("admin", "admin");
			SceneManager.ChangeScene("LoginScene");	
		});
	}

	public static void setAddCustomer(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			Client.Initialise();
			SceneManager.ChangeScene("ClientScene");
		});
	}

	public static void setAbort(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			SceneManager.ChangeScene("EmployeeScene");
		});
	}

	public static void setAddNewCustomer(Node node) {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;
			if (Client.CheckForErrors())
				Client.AddNewClient();
		});
	}

}
