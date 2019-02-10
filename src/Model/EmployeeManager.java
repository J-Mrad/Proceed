package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Controller.DatabaseHandler;
import Controller.EventManager;
import View.LayoutManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

@SuppressWarnings({ "deprecation", "unchecked" })
public abstract class EmployeeManager {
	private static ListView<HBox> EmployeeListview;
	private static Button AddEmployeeButton;
	private static TextField Name, Phone, Email, DateOfBirth, ShiftDuration, DateHired;
	private static ComboBox<String> Position;
	private static CheckBox isShowFired;
	private static Map<String, Integer> PositionsMap;
	private static Map<Integer, String> ReversePositionsMap;
	private static ImageView xView,LogoutButton;
	private static int ChosenEmployeeID = -1;
	private static boolean first = true;

	public static void Initialise() {
		LookUpNodes();
		FillList();
		SetupAddButton();
		SetupComboBox();
		SetupCheckbox();
		AddFilters();
		EventManager.setLogout(LogoutButton);
	}

	public static void Edit(int ID) {
		try {
		System.out.println("" + ID);
			ResultSet set = DatabaseHandler.Select("SELECT * FROM EMPLOYEE WHERE EID ='" + ID + "'");
			if (!set.next())
				return;
			Object object = new Object();
			Name.setText((object = set.getString(3)) == null ? "N/A" : object.toString());
			Phone.setText((object = set.getString(4)) == null ? "N/A" : object.toString());
			Email.setText((object = set.getString(5)) == null ? "N/A" : object.toString());
			DateOfBirth.setText(CorrectDate(set.getDate(6)));
			ShiftDuration.setText((object = set.getInt(7)) == null ? "N/A" : object.toString());
			DateHired.setText(CorrectDate(set.getDate(8)));

			Position.getSelectionModel().select(ReversePositionsMap.get(set.getInt(2)));

			AddEmployeeButton.setText("Apply Changes");
			xView.setVisible(true);

			EventManager.setResetEmployeeManagerEvent(xView);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void SetupCheckbox() {
		EventManager.setShowFiredEmployees(isShowFired);
	}

	private static void SetupComboBox() {
		try {
			PositionsMap = new HashMap<>();
			ReversePositionsMap = new HashMap<>();
			ObservableList<String> Positions = FXCollections.observableArrayList();
			ResultSet set = DatabaseHandler.Select("SELECT * FROM POSITION");

			while (set.next()) {
				Positions.add(set.getString(2));
				PositionsMap.put(set.getString(2), set.getInt(1));
				ReversePositionsMap.put(set.getInt(1), set.getString(2));
			}

			Position.setItems(Positions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void SetupAddButton() {
		AddEmployeeButton.setStyle(Constants.ADD_EMPLOYEE_STYLE);
		AddEmployeeButton.setTextFill(Constants.PROCEED_ORANGE);
		EventManager.setAddEmployeeOrApplyChanges(AddEmployeeButton);
	}

	public static void FillList() {
		String Query = "SELECT * FROM EMPLOYEE";
		if (first) {
			first = false;
			Query += " WHERE POSID <> 1";
		} else if (isShowFired.isSelected())
			Query += " WHERE POSID <> 1";

		Query += " ORDER BY NAME"; 
		
		ObservableList<HBox> Employees = FXCollections.observableArrayList();
		ResultSet set = DatabaseHandler.Select(Query);

		try {
			while (set.next()) {
				Employees.add(BuildBox(set.getInt(1), set.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		EmployeeListview.setItems(Employees);
	}

	private static HBox BuildBox(int ID, String info) {
		Label Name = new Label(info);
		Region Reg = new Region();
		Button Edit = new Button("Edit");
		EventManager.addToEmployeeEditEvent(Edit, ID);
		HBox.setHgrow(Reg, Priority.ALWAYS);
		Edit.setStyle(Constants.EDIT_EMPLOYEE_STYLE);
		Edit.setTextFill(Constants.PROCEED_ORANGE);
		return new HBox(Name, Reg, Edit);
	}

	private static void LookUpNodes() {
		EmployeeListview = (ListView<HBox>) LayoutManager.lookUp("EmployeeManager", "EmployeeList");
		AddEmployeeButton = (Button) LayoutManager.lookUp("EmployeeManager", "AddEmployeeButton");
		Position = (ComboBox<String>) LayoutManager.lookUp("EmployeeManager", "Position_Edit");
		Name = (TextField) LayoutManager.lookUp("EmployeeManager", "Name_Edit");
		Phone = (TextField) LayoutManager.lookUp("EmployeeManager", "Phone_Edit");
		Email = (TextField) LayoutManager.lookUp("EmployeeManager", "Email_Edit");
		DateOfBirth = (TextField) LayoutManager.lookUp("EmployeeManager", "DOB_Edit");
		ShiftDuration = (TextField) LayoutManager.lookUp("EmployeeManager", "Shirt_Duration_Edit");
		DateHired = (TextField) LayoutManager.lookUp("EmployeeManager", "Date_Hired_Edit");
		xView = (ImageView) LayoutManager.lookUp("EmployeeManager", "x_view");
		LogoutButton = (ImageView) LayoutManager.lookUp("EmployeeManager", "log_out");
		isShowFired = (CheckBox) LayoutManager.lookUp("EmployeeManager", "show_fired_checkbox");
	}

	public static void ApplyChanges() {
		DatabaseHandler.Query(
				"UPDATE EMPLOYEE SET POSID = '" + PositionsMap.get(Position.getSelectionModel().getSelectedItem())
						+ "'," + " NAME ='" + Name.getText() + "'," + " PHONE ='" + Phone.getText() + "'," + " EMAIL ='"
						+ (Email.getText().isEmpty() ? "N/A" : Email.getText()) + "',"
						+ ((DateOfBirth.getText().isEmpty() || DateOfBirth.getText().equals("N/A")) ? ""
								: " DOB =CAST('" + DateOfBirth.getText() + "' AS DATETIME),")
						+ " SHIFTDURATION ='" + Integer.parseInt(ShiftDuration.getText()) + "'," + " DATEHIRED =CAST('"
						+ DateHired.getText() + "' AS DATETIME)" + " WHERE EID=" + ChosenEmployeeID);
		Reset();
		FillList();
	}

	public static void AddEmployee() {
		DatabaseHandler.AddEmployee("{call admin.AddEmployee(?,?,?,?,?,?,?)}",
				PositionsMap.get(Position.getSelectionModel().getSelectedItem()), Name.getText(), Phone.getText(),
				((Email.getText().isEmpty() || Email.getText().equals("N/A")) ? "NULL" : Email.getText()),
				((DateOfBirth.getText().isEmpty() || DateOfBirth.getText().equals("N/A")) ? null
						: new java.sql.Date(Integer.parseInt(DateOfBirth.getText().split(" ")[0]),
								Integer.parseInt(DateOfBirth.getText().split(" ")[1]),
								Integer.parseInt(DateOfBirth.getText().split(" ")[2]))),
				Integer.parseInt(ShiftDuration.getText()),
				(DateHired.getText().equals("NOW")
						? new java.sql.Date(new Date(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).getTime())
						: new java.sql.Date(Calendar.getInstance().getTime().getTime())));
		
		Reset();
		FillList();
	}

	public static void Reset() {
		Position.getSelectionModel().clearSelection();
		Name.setText("");
		Phone.setText("");
		Email.setText("");
		DateOfBirth.setText("");
		ShiftDuration.setText("");
		DateHired.setText("");
		xView.setVisible(false);
		AddEmployeeButton.setText("Add Employee");
		setChosenEmployeeID(-1);
		Name.setStyle("");
		Phone.setStyle("");
		Email.setStyle("");
		DateOfBirth.setStyle("");
		ShiftDuration.setStyle("");
		DateHired.setStyle("");
		isShowFired.setSelected(false);
		first = true;
	}

	public static boolean CheckForErrors() {
		if (CheckPosition() && CheckName() && CheckPhone() && CheckEmail() && CheckDateOfBirth() && CheckShiftDuration()
				&& CheckDateHired())
			return true;
		return false;
	}

	public static boolean CheckPosition() {
		if (Position.getSelectionModel().isEmpty())
			return false;
		return true;
	}

	public static void setChosenEmployeeID(int ID) {
		ChosenEmployeeID = ID;
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

	private static boolean CheckShiftDuration() {
		try {
			if (!ShiftDuration.getText().isEmpty()
					&& (Double.parseDouble(ShiftDuration.getText()) < 15 && ShiftDuration.getText().length() < 3))
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private static boolean CheckDateHired() {
		if (DateHired.getText().equals("NOW") || (!DateHired.getText().isEmpty() && (DateHired.getText().matches(
				"^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
				&& DateHired.getText().length() < 11))
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
		ShiftDuration.setOnKeyTyped(event -> {
			if (CheckShiftDuration())
				ShiftDuration.setStyle("");
			else
				ShiftDuration.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		DateHired.setOnKeyTyped(event -> {
			if (CheckDateHired())
				DateHired.setStyle("");
			else
				DateHired.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
	}

	private static String CorrectDate(Date Date) {
		if (Date == null)
			return "N/A";
		String DD = Date.toString().split("-")[2];
		String MM = Date.toString().split("-")[1];
		String YYYY = Date.toString().split("-")[0];
		return DD + "-" + MM + "-" + YYYY;
	}

}
