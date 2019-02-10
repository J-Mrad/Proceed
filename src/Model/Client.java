package Model;

import java.text.SimpleDateFormat;
import java.util.Date;

import Controller.DatabaseHandler;
import Controller.EventManager;
import View.LayoutManager;
import View.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public abstract class Client {
	private static TextField Name, Phone, Email, DateOfBirth, TotalValue, TotalAccounts, TotalLoans, ClientSince;
	private static CheckBox isPremium;
	private static ImageView Abort;
	private static Button AddNewClient;

	public static void Initialise() {
		SetupNodes();
		SetupAddNewClient();
		SetupNodesEvents();
		AddFilters();

	}

	public static void AddNewClient() {
		DatabaseHandler.Query(
				"INSERT INTO CLIENT(NAME,PHONE,EMAIL,DOB,TOTALVALUE,TOTALACCOUNTS,TOTALLOANS,CLIENTSINCE,ISPREMIUM)"
						+ "VALUES('" + Name.getText() + "','" + Phone.getText() + "',"
						+ (Email.getText().isEmpty() ? "NULL" : ("'" + Email.getText() + "'")) + ","
						+ (DateOfBirth.getText().isEmpty() ? "NULL" : ("'" + DateOfBirth.getText() + "'")) + ","
						+ (TotalValue.getText().isEmpty() ? "0" : TotalValue.getText()) + ","
						+ (TotalAccounts.getText().isEmpty() ? "0" : TotalAccounts.getText()) + ","
						+ (TotalLoans.getText().isEmpty() ? "0" : TotalLoans.getText()) + ",'"
						+ (ClientSince.getText().equals("NOW") ? CorrectDate() : ClientSince.getText()) + "',"
						+ (isPremium.isSelected() ? "1" : "0") + ")");
		Reset();
		Employee.Initialise(Employee.getCurrentEID());
		SceneManager.ChangeScene("EmployeeScene");
	}

	private static void SetupAddNewClient() {
		AddNewClient.setStyle(Constants.ADD_NEW_CLIENT_STYLE);
		EventManager.setAddNewCustomer(AddNewClient);
	}

	private static void SetupNodesEvents() {
		EventManager.setAbort(Abort);

	}

	private static void SetupNodes() {
		Name = (TextField) LayoutManager.lookUp("Client", "client_name");
		Phone = (TextField) LayoutManager.lookUp("Client", "client_phone");
		Email = (TextField) LayoutManager.lookUp("Client", "client_email");
		DateOfBirth = (TextField) LayoutManager.lookUp("Client", "client_dob");
		TotalValue = (TextField) LayoutManager.lookUp("Client", "client_total_value");
		TotalAccounts = (TextField) LayoutManager.lookUp("Client", "client_total_accounts");
		TotalLoans = (TextField) LayoutManager.lookUp("Client", "client_total_loans");
		ClientSince = (TextField) LayoutManager.lookUp("Client", "client_client_since");
		isPremium = (CheckBox) LayoutManager.lookUp("Client", "client_is_premium");
		Abort = (ImageView) LayoutManager.lookUp("Client", "client_abort");
		AddNewClient = (Button) LayoutManager.lookUp("Client", "client_add_client");
	}

	public static void Reset() {
		Name.setText("");
		Phone.setText("");
		Email.setText("");
		DateOfBirth.setText("");
		TotalValue.setText("");
		TotalAccounts.setText("");
		TotalLoans.setText("");
		ClientSince.setText("");
		isPremium.setSelected(false);
		Name.setStyle("");
		Phone.setStyle("");
		Email.setStyle("");
		DateOfBirth.setStyle("");
		TotalValue.setStyle("");
		TotalAccounts.setStyle("");
		TotalLoans.setStyle("");
		ClientSince.setStyle("");
	}

	public static boolean CheckForErrors() {
		if (CheckName() && CheckPhone() && CheckEmail() && CheckDateOfBirth() && CheckTotalValue()
				&& CheckTotalAccounts() && CheckTotalLoans() && CheckClientSince())
			return true;
		return false;
	}

	private static boolean CheckName() {
		if (Name.getText().isEmpty() || Name.getText().split(" ")[0].length() < 3
				|| !Name.getText().matches("^[A-Za-z0-9 _]*[A-Za-z0-9][A-Za-z0-9 _]*$")
				|| Name.getText().length() >= 20)
			return false;

		return true;
	}

	private static boolean CheckPhone() {
		if ((!Phone.getText().isEmpty() && Phone.getText().matches("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$"))
				&& Phone.getText().length() < 15)
			return true;
		return false;
	}

	private static boolean CheckEmail() {
		if (Email.getText().equals("N/A") || Email.getText().isEmpty() || (Email.getText().matches(
				"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
				&& Email.getText().length() < 20))
			return true;

		return true;
	}

	private static boolean CheckDateOfBirth() {

		if (DateOfBirth.getText().equals("N/A") || (DateOfBirth.getText().isEmpty() || DateOfBirth.getText().matches(
				"^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
				&& DateOfBirth.getText().length() < 11)
			return true;
		return false;
	}

	private static boolean CheckTotalValue() {
		try {
			if (!TotalValue.getText().isEmpty() && Double.parseDouble(TotalValue.getText()) >= 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private static boolean CheckTotalAccounts() {
		try {
			if (!TotalAccounts.getText().isEmpty() && Double.parseDouble(TotalAccounts.getText()) >= 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private static boolean CheckTotalLoans() {
		try {
			if (!TotalLoans.getText().isEmpty() && Double.parseDouble(TotalLoans.getText()) >= 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private static boolean CheckClientSince() {
		if (ClientSince.getText().equals("NOW") || (!ClientSince.getText().isEmpty() && (ClientSince.getText().matches(
				"^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
				&& ClientSince.getText().length() < 11))
			return true;
		return false;
	}

	public static void AddFilters() {
		Name.setOnKeyTyped(event -> {
			if (CheckName())
				Name.setStyle("");
			else
				Name.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		Phone.setOnKeyTyped(event -> {
			if (CheckPhone())
				Phone.setStyle("");
			else
				Phone.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		Email.setOnKeyTyped(event -> {
			if (CheckEmail())
				Email.setStyle("");
			else
				Email.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		DateOfBirth.setOnKeyTyped(event -> {
			if (CheckDateOfBirth())
				DateOfBirth.setStyle("");
			else
				DateOfBirth.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		TotalAccounts.setOnKeyTyped(event -> {
			if (CheckTotalAccounts())
				TotalAccounts.setStyle("");
			else
				TotalAccounts.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		TotalLoans.setOnKeyTyped(event -> {
			if (CheckTotalLoans())
				TotalLoans.setStyle("");
			else
				TotalLoans.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		TotalValue.setOnKeyTyped(event -> {
			if (CheckTotalValue())
				TotalValue.setStyle("");
			else
				TotalValue.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		ClientSince.setOnKeyTyped(event -> {
			if (CheckClientSince())
				ClientSince.setStyle("");
			else
				ClientSince.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
	}

	private static String CorrectDate() {
		return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}
}
