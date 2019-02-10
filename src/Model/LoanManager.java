package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Controller.DatabaseHandler;
import Controller.EventManager;
import View.LayoutManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
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
public abstract class LoanManager {
	private static ListView<HBox> ClientListview;
	private static Button Loan;
	private static TextField ClientID, DateTaken, NextPayment, SplitInto, CycleDays, TotalPayment, PaidSoFar,
			RemainingDue;
	private static ImageView xView, LogoutButton;
	private static int CurrentPID12 = -1;

	public static void Initialise() {
		LookUpNodes();
		FillList();
		SetupAddButton();
		AddFilters();
		EventManager.setLogout(LogoutButton);
		xView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			Reset();
		});
		xView.setVisible(false);
	}

	public static void Edit(int ID) {
		try {
			ResultSet set = DatabaseHandler
					.Select("SELECT TOP 1 * FROM LOAN WHERE CID ='" + ID + "' ORDER BY PID12 DESC");
			Object object = new Object();
			if (!set.next()) {
				set = DatabaseHandler.Select("SELECT * FROM CLIENT WHERE CID='" + ID + "'");
				set.next();
				ClientID.setText((object = set.getInt(1)) == null ? "N/A" : object.toString());
				DateTaken.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
			} else {
				ClientID.setText((object = set.getInt(2)) == null ? "N/A" : object.toString());
				DateTaken.setText((object = set.getDate(3)) == null ? "N/A" : CorrectDate((Date) object));
				NextPayment.setText((object = set.getInt(4)) == null ? "N/A" : object.toString());
				SplitInto.setText((object = set.getInt(5)) == null ? "N/A" : object.toString());
				CycleDays.setText((object = set.getInt(6)) == null ? "N/A" : object.toString());
				TotalPayment.setText((object = set.getInt(7)) == null ? "N/A" : object.toString());
				PaidSoFar.setText((object = set.getInt(8)) == null ? "N/A" : object.toString());
				RemainingDue.setText((object = set.getInt(9)) == null ? "N/A" : object.toString());
				Loan.setText("Apply Changes");
			}
			xView.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void SetupAddButton() {
		Loan.setStyle(Constants.ADD_EMPLOYEE_STYLE);
		Loan.setTextFill(Constants.PROCEED_ORANGE);
		Loan.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			if (Loan.getText().equals("Loan"))
				AddLoan();
			else if (Loan.getText().equals("Apply Changes"))
				ApplyChanges();

		});

	}

	public static void FillList() {
		String Query = "SELECT * FROM CLIENT ORDER BY NAME";
		ObservableList<HBox> Clients = FXCollections.observableArrayList();
		ResultSet set = DatabaseHandler.Select(Query);
		try {
			while (set.next()) {
				Clients.add(BuildBox(set.getInt(1), set.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClientListview.setItems(Clients);
	}

	private static HBox BuildBox(int ID, String info) {
		try {
			ResultSet set = DatabaseHandler.Select("SELECT * FROM LOAN WHERE CID ='" + ID + "'");
			Label Name = new Label(info);
			Region Reg = new Region();
			Button Edit = new Button();
			if (set.next())
				Edit.setText("Edit");
			else
				Edit.setText("Add");
			Edit.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				if (event.getButton() != MouseButton.PRIMARY)
					return;
				Edit(ID);
			});
			HBox.setHgrow(Reg, Priority.ALWAYS);
			Edit.setStyle(Constants.EDIT_EMPLOYEE_STYLE);
			Edit.setTextFill(Constants.PROCEED_ORANGE);
			return new HBox(Name, Reg, Edit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void ApplyChanges() {

		DatabaseHandler.Query(
				"UPDATE LOAN SET DATETAKEN = '" + DateTaken.getText() + "', NEXTPAYMENT =" + NextPayment.getText() + " "
						+ ((SplitInto.getText().isEmpty() || SplitInto.getText().equals("N/A")) ? ""
								: (", SPLITINTO = " + SplitInto.getText() + " "))
						+ ""
						+ ((CycleDays.getText().isEmpty() || CycleDays.getText().equals("N/A")) ? ""
								: (", CYCLEDAYS = " + CycleDays.getText() + " "))
						+ ""
						+ ((TotalPayment.getText().isEmpty() || TotalPayment.getText().equals("N/A")) ? ""
								: (", TOTALPAYMENTS = " + TotalPayment.getText() + " "))
						+ ""
						+ ((PaidSoFar.getText().isEmpty() || PaidSoFar.getText().equals("N/A")) ? ""
								: (", PAIDSOFAR = " + PaidSoFar.getText() + " "))
						+ ""
						+ ((RemainingDue.getText().isEmpty() || RemainingDue.getText().equals("N/A")) ? ""
								: (", REMAININGDUE = " + RemainingDue.getText() + " "))
						+ "" + " WHERE PID12 = " + CurrentPID12);
		Reset();
		FillList();
	}

	public static void AddLoan() {

		DatabaseHandler.Query(
				"INSERT INTO LOAN(CID,DATETAKEN,NEXTPAYMENT,SPLITINTO,CYCLEDAYS,TOTALPAYMENTS,PAIDSOFAR,REMAININGDUE) "
						+ "VALUES(" + ClientID.getText() + ",'" + DateTaken.getText() + "'," + NextPayment.getText()
						+ ","
						+ ((SplitInto.getText().isEmpty() || SplitInto.getText().equals("N/A")) ? "NULL"
								: SplitInto.getText())
						+ ","
						+ ((CycleDays.getText().isEmpty() || CycleDays.getText().equals("N/A")) ? "NULL"
								: CycleDays.getText())
						+ ","
						+ ((TotalPayment.getText().isEmpty() || TotalPayment.getText().equals("N/A")) ? "NULL"
								: TotalPayment.getText())
						+ ","
						+ ((PaidSoFar.getText().isEmpty() || PaidSoFar.getText().equals("N/A")) ? "NULL"
								: PaidSoFar.getText())
						+ "," + ((RemainingDue.getText().isEmpty() || RemainingDue.getText().equals("N/A")) ? "NULL"
								: RemainingDue.getText())
						+ ")");

		Reset();
		FillList();
	}

	public static void Reset() {
		ClientID.setText("");
		DateTaken.setText("");
		NextPayment.setText("");
		SplitInto.setText("");
		CycleDays.setText("");
		TotalPayment.setText("");
		PaidSoFar.setText("");
		RemainingDue.setText("");
		xView.setVisible(false);
		Loan.setText("Loan");
		ClientID.setStyle("");
		DateTaken.setStyle("");
		NextPayment.setStyle("");
		SplitInto.setStyle("");
		CycleDays.setStyle("");
		TotalPayment.setStyle("");
		PaidSoFar.setStyle("");
		RemainingDue.setStyle("");
		CurrentPID12 = -1;
	}

	public static boolean CheckForErrors() {
		if (CheckClientID() && CheckDateTaken() && CheckNextPayment() && CheckSplitInto() && CheckCycleDays()
				&& CheckTotalPayment() && CheckPaidSoFar() && CheckRemainingDue())
			return true;
		return false;
	}

	private static boolean CheckClientID() {
		try {
			Integer.parseInt(ClientID.getText());
			if (ClientID.getText().equals("N/A") || !ClientID.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckDateTaken() {
		if (SplitInto.getText().equals("N/A") || (SplitInto.getText().isEmpty() || SplitInto.getText().matches(
				"^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
				&& SplitInto.getText().length() < 11)
			return true;
		return false;
	}

	private static boolean CheckNextPayment() {
		try {
			Integer.parseInt(NextPayment.getText());
			if (ClientID.getText().equals("N/A") ||!NextPayment.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckSplitInto() {
		try {
			Integer.parseInt(SplitInto.getText());
			if (SplitInto.getText().equals("N/A") || !SplitInto.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;

	}

	private static boolean CheckCycleDays() {
		try {
			Integer.parseInt(CycleDays.getText());
			if (CycleDays.getText().equals("N/A") || !CycleDays.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckTotalPayment() {
		try {
			Integer.parseInt(TotalPayment.getText());
			if (TotalPayment.getText().equals("N/A") || !TotalPayment.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckPaidSoFar() {
		try {
			Integer.parseInt(PaidSoFar.getText());
			if (PaidSoFar.getText().equals("N/A") || !PaidSoFar.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckRemainingDue() {
		try {
			Integer.parseInt(RemainingDue.getText());
			if (RemainingDue.getText().equals("N/A") || !RemainingDue.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static void AddFilters() {
		ClientID.setOnKeyTyped(event -> {
			if (CheckClientID())
				ClientID.setStyle("");
			else
				ClientID.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		DateTaken.setOnKeyTyped(event -> {
			if (CheckDateTaken())
				DateTaken.setStyle("");
			else
				DateTaken.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		NextPayment.setOnKeyTyped(event -> {
			if (CheckNextPayment())
				NextPayment.setStyle("");
			else
				NextPayment.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		SplitInto.setOnKeyTyped(event -> {
			if (CheckSplitInto())
				SplitInto.setStyle("");
			else
				SplitInto.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		CycleDays.setOnKeyTyped(event -> {
			if (CheckCycleDays())
				CycleDays.setStyle("");
			else
				CycleDays.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		TotalPayment.setOnKeyTyped(event -> {
			if (CheckTotalPayment())
				TotalPayment.setStyle("");
			else
				TotalPayment.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		PaidSoFar.setOnKeyTyped(event -> {
			if (CheckPaidSoFar())
				PaidSoFar.setStyle("");
			else
				PaidSoFar.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		RemainingDue.setOnKeyTyped(event -> {
			if (CheckRemainingDue())
				RemainingDue.setStyle("");
			else
				RemainingDue.setStyle(Constants.RED_ERROR_TEXTFIELD);
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

	private static void LookUpNodes() {
		ClientListview = (ListView<HBox>) LayoutManager.lookUp("LoanManager", "client_list");
		Loan = (Button) LayoutManager.lookUp("LoanManager", "loan_button");
		ClientID = (TextField) LayoutManager.lookUp("LoanManager", "client_id");
		DateTaken = (TextField) LayoutManager.lookUp("LoanManager", "date_taken");
		NextPayment = (TextField) LayoutManager.lookUp("LoanManager", "next_payment");
		SplitInto = (TextField) LayoutManager.lookUp("LoanManager", "split_into");
		CycleDays = (TextField) LayoutManager.lookUp("LoanManager", "cycle_days");
		TotalPayment = (TextField) LayoutManager.lookUp("LoanManager", "total_payment");
		PaidSoFar = (TextField) LayoutManager.lookUp("LoanManager", "paid_so_far");
		RemainingDue = (TextField) LayoutManager.lookUp("LoanManager", "remaining_due");
		xView = (ImageView) LayoutManager.lookUp("LoanManager", "x_view");
		LogoutButton = (ImageView) LayoutManager.lookUp("LoanManager", "log_out");
	}
}
