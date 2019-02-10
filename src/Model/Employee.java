package Model;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Controller.DatabaseHandler;
import Controller.EventManager;
import View.LayoutManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

@SuppressWarnings("unchecked")
public abstract class Employee {
	private static int CurrentEID, CurrentCID = -1;
	private static ImageView LogOut, AddCustomer, XView;
	private static ListView<HBox> Clients;
	private static ComboBox<String> AccountType, FromAccount, ToAccount;
	private static ComboBox<Integer> AccountLevel;
	private static TextField From, To, Quantity;
	private static Button AddNewAccountButton, Transfer;
	private static Label ClientName, ClientLabel;

	public static void Initialise(int EID) {
		CurrentEID = EID;
		SetupNodes();
		SetupNodesEvents();
		SetupAccountTypes();
		FillList();
	}

	private static void SetupNodesEvents() {
		EventManager.setLogout(LogOut);
		EventManager.setAddCustomer(AddCustomer);
		XView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;
			Reset();
		});

		AddNewAccountButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY || CurrentCID == -1)
				return;

			try {
				DatabaseHandler.Query("INSERT INTO ACCOUNT(LEVEL,DATECREATED,CURRENCY) " + "VALUES("
						+ AccountLevel.getSelectionModel().getSelectedItem() + ", '" + CorrectDate() + "','USD')");

				ResultSet set = DatabaseHandler.Select("SELECT TOP 1 * FROM ACCOUNT ORDER BY PID10 DESC");
				set.next();
				int PID10 = set.getInt(1);

				DatabaseHandler.Query("INSERT INTO CLIENTACCOUNT(PID10,CID,EID,ISACTIVE,ISSHARED) " + "VALUES(" + PID10
						+ "," + CurrentCID + "," + CurrentEID + ",1,0)");

				switch (AccountType.getSelectionModel().getSelectedItem()) {
				case Constants.ACCOUNT_TYPE_CREDIT: {
					DatabaseHandler.Query("INSERT INTO CREDITACCOUNT(PID10,CYCLEDAYS,AMMOUNTDUE,NEXTDUEDATE,ISOVERDUE) "
							+ "VALUES(" + PID10 + ",30,0,'" + CorrectNextMonthDate() + "',0)");
					break;
				}
				case Constants.ACCOUNT_TYPE_DEBIT: {
					DatabaseHandler.Query("INSERT INTO DEBITACCOUNT(PID10,BALANCE,TOTALDEPOSIT,TOTALWITHDRAW) "
							+ "VALUES(" + PID10 + ",0,0,0)");
					break;
				}
				case Constants.ACCOUNT_TYPE_SAVINGS: {
					DatabaseHandler.Query(
							"INSERT INTO SAVINGSACCOUNT(PID10,BALANCE,WITHDRAWALS,WITHDRAWLIMIT,CYCLEDAYS,NEXTREFRESH) "
									+ "VALUES(" + PID10 + ",0,0,15," + 30 + ",'" + CorrectNextMonthDate() + "')");
					break;
				}
				case Constants.ACCOUNT_TYPE_RETIREMENT: {
					DatabaseHandler.Query("INSERT INTO RETIREMENTACCOUNT(PID10,BALANCE,DUEDATE) " + "VALUES(" + PID10
							+ "0,'" + CorrectNextMonthDate() + "')");
					break;
				}
				case Constants.ACCOUNT_TYPE_YOUTH: {
					DatabaseHandler.Query("INSERT INTO YOUTHACCOUNT(PID10,SWITCHDATE) " + "VALUES(" + PID10 + ",'"
							+ CorrectNextYearDate() + "')");
					break;
				}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Transfer.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			System.out.println( CheckForErrors());

			if (CheckForErrors())
				DatabaseHandler.Transfer(
						Integer.parseInt(FromAccount.getSelectionModel().getSelectedItem().split(" ")[1]),
						Integer.parseInt(ToAccount.getSelectionModel().getSelectedItem().split(" ")[1]),
						Integer.parseInt(Quantity.getText()));
		});

	}

	private static boolean CheckForErrors() {
		try {
			Integer.parseInt(From.getText());
			Integer.parseInt(To.getText());
			if (FromAccount.getSelectionModel().getSelectedItem() != null
					&& ToAccount.getSelectionModel().getSelectedItem() != null)
				return true;
		} catch (Exception e) {

		}
		return false;

	}

	private static void SetupNodes() {
		LogOut = (ImageView) LayoutManager.lookUp("Employee", "employee_log_out");
		AddCustomer = (ImageView) LayoutManager.lookUp("Employee", "employee_add_client");
		XView = (ImageView) LayoutManager.lookUp("Employee", "x_view");
		Clients = (ListView<HBox>) LayoutManager.lookUp("Employee", "employee_client_list_view");
		From = (TextField) LayoutManager.lookUp("Employee", "employee_tranfer_from");
		To = (TextField) LayoutManager.lookUp("Employee", "employee_transfer_to");
		Quantity = (TextField) LayoutManager.lookUp("Employee", "employee_quantity");
		AccountType = (ComboBox<String>) LayoutManager.lookUp("Employee", "employee_account_type");
		FromAccount = (ComboBox<String>) LayoutManager.lookUp("Employee", "empoyee_from_account");
		AccountLevel = (ComboBox<Integer>) LayoutManager.lookUp("Employee", "employee_account_level");
		ToAccount = (ComboBox<String>) LayoutManager.lookUp("Employee", "empoyee_to_account");
		AddNewAccountButton = (Button) LayoutManager.lookUp("Employee", "employee_add_new_account_button");
		Transfer = (Button) LayoutManager.lookUp("Employee", "employee_transfer");
		ClientName = (Label) LayoutManager.lookUp("Employee", "employee_client_name");
		ClientLabel = (Label) LayoutManager.lookUp("Employee", "employee_client_label");
		ClientLabel.setVisible(false);
		AddNewAccountButton.setStyle(Constants.ADD_ACCOUNT_STYLE);
		Transfer.setStyle(Constants.TRANSFER_STYLE);
	}

	public static void Reset() {
		From.setText("");
		To.setText("");
		Quantity.setText("");
		FromAccount.setItems(null);
		ToAccount.setItems(null);
		ClientLabel.setVisible(false);
		ClientName.setText("");
		CurrentCID = -1;
	}

	private static void SetupAccountTypes() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll(Constants.ACCOUNT_TYPES);
		AccountType.setItems(list);
		AccountType.getSelectionModel().select(0);

		ObservableList<Integer> list2 = FXCollections.observableArrayList();
		for (int i = 0; i <= 6; i++)
			list2.add(i);

		AccountLevel.setItems(list2);
		AccountLevel.getSelectionModel().select(0);

	}

	public static int getCurrentEID() {
		return CurrentEID;
	}

	private static void FillList() {
		try {
			ObservableList<HBox> list = FXCollections.observableArrayList();
			ResultSet set = DatabaseHandler.Select("SELECT * FROM CLIENT ORDER BY NAME");

			while (set.next())
				list.add(BuildBox(set.getInt(1), set.getString(2)));

			Clients.setItems(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HBox BuildBox(int ID, String info) {
		Label Name = new Label(info);
		Region Reg = new Region();
		Button Edit = new Button("Edit");
		HBox Box = new HBox(Name, Reg, Edit);
		AddBoxEvents(Box, Edit, ID);
		HBox.setHgrow(Reg, Priority.ALWAYS);
		Edit.setStyle(Constants.EDIT_EMPLOYEE_STYLE);
		Edit.setTextFill(Constants.PROCEED_ORANGE);
		return Box;
	}

	private static void AddBoxEvents(HBox box, Button Edit, int ID) {
		Edit.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;
			EditClient(ID);
		});

		box.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (event.getButton() != MouseButton.PRIMARY)
				return;

			if (Check(From, event.getPickResult().getIntersectedNode())) {
				if (!To.getText().equals(ID + "")) {
					From.setText(ID + "");
					try {
						ObservableList<String> list = FXCollections.observableArrayList();
						ResultSet set = DatabaseHandler
								.Select("SELECT CLIENTACCOUNT.PID10 FROM CLIENTACCOUNT,DEBITACCOUNT WHERE CID ='" + ID
										+ "' AND CLIENTACCOUNT.PID10 = DEBITACCOUNT.PID10");
						while (set.next())
							list.add("DEBITACCOUNT " + set.getInt(1));

						FromAccount.setItems(list);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else if (Check(To, event.getPickResult().getIntersectedNode())) {
				if (!From.getText().equals(ID + "")) {
					To.setText(ID + "");
					try {
						ObservableList<String> list = FXCollections.observableArrayList();
						ResultSet set = DatabaseHandler
								.Select("SELECT CLIENTACCOUNT.PID10 FROM CLIENTACCOUNT,DEBITACCOUNT WHERE CID ='" + ID
										+ "' AND CLIENTACCOUNT.PID10 = DEBITACCOUNT.PID10");
						while (set.next())
							list.add("DEBITACCOUNT " + set.getInt(1));

						ToAccount.setItems(list);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	private static boolean Check(TextField textField, Node node) {
		if (textField.equals(node) || textField.getParent().equals(node))
			return true;
		for (Node node2 : textField.getParent().getChildrenUnmodifiable()) {
			if (node2.equals(node))
				return true;
		}
		for (Node node2 : textField.getChildrenUnmodifiable()) {
			if (node2.equals(node))
				return true;
		}
		return false;

	}

	private static void EditClient(int ID) {
		try {
			ResultSet set = DatabaseHandler.Select("SELECT NAME FROM CLIENT WHERE CID = '" + ID + "'");
			set.next();
			CurrentCID = ID;
			ClientName.setText(set.getString(1));
			ClientLabel.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String CorrectDate() {
		return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}

	private static String CorrectNextMonthDate() {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date nextMonthFirstDay = calendar.getTime();
		return new SimpleDateFormat("dd-MM-yyyy").format(nextMonthFirstDay);
	}

	private static String CorrectNextYearDate() {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
		Date nextYearFirstDay = calendar.getTime();
		return new SimpleDateFormat("dd-MM-yyyy").format(nextYearFirstDay);
	}
}
