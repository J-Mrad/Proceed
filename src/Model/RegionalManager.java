package Model;

import java.sql.ResultSet;

import Controller.DatabaseHandler;
import Controller.EventManager;
import View.LayoutManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("unchecked")

public abstract class RegionalManager {

	private static Button Update;
	private static TextField MinBalance, MaxDue, MaxWithdraw, InterestDebit, InterestGain, TaxCredit;
	private static ComboBox<Integer> AccountLevel;
	private static ImageView LogoutButton;

	public static void Initialise() {
		LookUpNodes();
		SetupAddButton();
		SetupComboBox();
		AddFilters();
		EventManager.setLogout(LogoutButton);
	}

	public static void LoadValues(int ID) {
		try {
			ResultSet set = DatabaseHandler.Select("SELECT * FROM ACCOUNTLEVEL WHERE LEVEL ='" + ID + "'");
			if (!set.next())
				return;

			Object object = new Object();
			MinBalance.setText((object = set.getInt(2)) == null ? "N/A" : object.toString());
			MaxDue.setText((object = set.getInt(3)) == null ? "N/A" : object.toString());
			MaxWithdraw.setText((object = set.getInt(4)) == null ? "N/A" : object.toString());
			InterestDebit.setText((object = set.getInt(5)) == null ? "N/A" : object.toString());
			InterestGain.setText((object = set.getInt(6)) == null ? "N/A" : object.toString());
			TaxCredit.setText((object = set.getInt(7)) == null ? "N/A" : object.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void SetupComboBox() {
		try {
			ObservableList<Integer> Levels = FXCollections.observableArrayList();
			ResultSet set = DatabaseHandler.Select("SELECT * FROM ACCOUNTLEVEL");

			while (set.next())
				Levels.add(set.getInt(1));

			AccountLevel.setItems(Levels);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AccountLevel.valueProperty().addListener(listener -> {
			if (AccountLevel.getSelectionModel().getSelectedItem() != null)
				LoadValues(AccountLevel.getSelectionModel().getSelectedItem());
		});
	}

	private static void SetupAddButton() {
		Update.setStyle(Constants.ADD_EMPLOYEE_STYLE);
		Update.setTextFill(Constants.PROCEED_ORANGE);
		Update.addEventFilter(MouseEvent.MOUSE_PRESSED, Event -> {
			if (Event.getButton() != MouseButton.PRIMARY)
				return;
			if (AccountLevel.getSelectionModel().getSelectedItem() != null)
				ApplyChanges();
		});
	}

	private static void LookUpNodes() {
		Update = (Button) LayoutManager.lookUp("RegionalManager", "update_button");
		AccountLevel = (ComboBox<Integer>) LayoutManager.lookUp("RegionalManager", "account_level");
		MinBalance = (TextField) LayoutManager.lookUp("RegionalManager", "min_balance");
		MaxDue = (TextField) LayoutManager.lookUp("RegionalManager", "max_due");
		MaxWithdraw = (TextField) LayoutManager.lookUp("RegionalManager", "max_withdraw");
		InterestDebit = (TextField) LayoutManager.lookUp("RegionalManager", "interest_debit");
		InterestGain = (TextField) LayoutManager.lookUp("RegionalManager", "interest_gain");
		TaxCredit = (TextField) LayoutManager.lookUp("RegionalManager", "tax_credit");
		LogoutButton = (ImageView) LayoutManager.lookUp("RegionalManager", "log_out");
	}

	public static void ApplyChanges() {

		DatabaseHandler.Query("UPDATE ACCOUNTLEVEL SET MINBALANCE = " + MinBalance.getText() + ", MAXDUE ="
				+ MaxDue.getText() + ", MAXWITHDRAW = " + MaxWithdraw.getText()
				+ ((InterestDebit.getText().isEmpty() || InterestDebit.getText().equals("N/A")) ? ""
						: (", INTERESTDEBIT =" + InterestDebit.getText()))
				+ ((InterestGain.getText().isEmpty() || InterestGain.getText().equals("N/A")) ? ""
						: (", INTERESTGAIN =" + InterestGain.getText()))
				+ ((TaxCredit.getText().isEmpty() || TaxCredit.getText().equals("N/A")) ? ""
						: (", TAXCREDIT =" + TaxCredit.getText()))
				+ " WHERE LEVEL = " + AccountLevel.getSelectionModel().getSelectedItem());
		Reset();
	}

	public static void Reset() {
		AccountLevel.getSelectionModel().select(null);
		MinBalance.setText("");
		MaxDue.setText("");
		MaxWithdraw.setText("");
		InterestDebit.setText("");
		InterestGain.setText("");
		TaxCredit.setText("");
		MinBalance.setStyle("");
		MaxDue.setStyle("");
		MaxWithdraw.setStyle("");
		InterestDebit.setStyle("");
		InterestGain.setStyle("");
		TaxCredit.setStyle("");

	}

	public static boolean CheckForErrors() {
		if (CheckMinBalance() && CheckMaxDue() && CheckMaxWithdraw() && CheckInterestDebit() && CheckInterestGain()
				&& CheckTaxCredit())
			return true;
		return false;
	}

	public static boolean CheckMinBalance() {
		try {
			Integer.parseInt(MinBalance.getText());
			if (MinBalance.getText().equals("N/A") || !MinBalance.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckMaxDue() {
		try {
			Integer.parseInt(MaxDue.getText());
			if (MaxDue.getText().equals("N/A") || !MaxDue.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckMaxWithdraw() {
		try {
			Integer.parseInt(MaxWithdraw.getText());
			if (MaxWithdraw.getText().equals("N/A") || !MaxWithdraw.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckInterestDebit() {
		try {
			Integer.parseInt(InterestDebit.getText());
			if (InterestDebit.getText().equals("N/A") || !InterestDebit.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckInterestGain() {
		try {
			Integer.parseInt(InterestGain.getText());
			if (InterestGain.getText().equals("N/A") || !InterestGain.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private static boolean CheckTaxCredit() {
		try {
			Integer.parseInt(TaxCredit.getText());
			if (TaxCredit.getText().equals("N/A") || !TaxCredit.getText().isEmpty())
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static void AddFilters() {
		MinBalance.setOnKeyTyped(event -> {
			if (CheckMinBalance())
				MinBalance.setStyle("");
			else
				MinBalance.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		MaxDue.setOnKeyTyped(event -> {
			if (CheckMaxDue())
				MaxDue.setStyle("");
			else
				MaxDue.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		MaxWithdraw.setOnKeyTyped(event -> {
			if (CheckMaxWithdraw())
				MaxWithdraw.setStyle("");
			else
				MaxWithdraw.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		InterestDebit.setOnKeyTyped(event -> {
			if (CheckInterestDebit())
				InterestDebit.setStyle("");
			else
				InterestDebit.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		InterestGain.setOnKeyTyped(event -> {
			if (CheckInterestGain())
				InterestGain.setStyle("");
			else
				InterestGain.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
		TaxCredit.setOnKeyTyped(event -> {
			if (CheckTaxCredit())
				TaxCredit.setStyle("");
			else
				TaxCredit.setStyle(Constants.RED_ERROR_TEXTFIELD);
		});
	}

}
