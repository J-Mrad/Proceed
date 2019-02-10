package Model;

import javafx.scene.paint.Color;

public abstract class Constants {

	public static final String WORKING_DIRECTORY = System.getProperty("user.dir");
	public static final String MODEL_DIRECTORY_URL = "file:///" + System.getProperty("user.dir").replaceAll("\\\\", "/")
			+ "/src/View/";
	public static final String APP_NAME = "Proceed";

	public static final String ADD_EMPLOYEE_STYLE = "-fx-background-image: url('" + MODEL_DIRECTORY_URL
			+ "/AddEmployeeButton.png')";
	public static final String ADD_NEW_CLIENT_STYLE = "-fx-background-image: url('" + MODEL_DIRECTORY_URL
			+ "/AddNewClient.png')";
	public static final String EDIT_EMPLOYEE_STYLE = "-fx-background-image: url('" + MODEL_DIRECTORY_URL
			+ "/EditEmployeeButton.png')";
	public static final String ADD_ACCOUNT_STYLE = "-fx-background-image: url('" + MODEL_DIRECTORY_URL
			+ "/AddAccount.png')";
	public static final String TRANSFER_STYLE = "-fx-background-image: url('" + MODEL_DIRECTORY_URL
			+ "/Transfer.png')";
	public static final String RED_ERROR_TEXTFIELD = "-fx-text-box-border: red ; -fx-focus-color: red ;";

	public static final Color BACKGROUND_COLOR = Color.ANTIQUEWHITE, PROCEED_ORANGE = Color.rgb(237, 104, 38);

	public static final String ACCOUNT_TYPE_CREDIT = "Credit", ACCOUNT_TYPE_DEBIT = "Debit",
			ACCOUNT_TYPE_SAVINGS = "Savings", ACCOUNT_TYPE_RETIREMENT = "Retirement", ACCOUNT_TYPE_YOUTH = "Youth";

	public static final String[] ACCOUNT_TYPES = { ACCOUNT_TYPE_CREDIT, ACCOUNT_TYPE_DEBIT, ACCOUNT_TYPE_SAVINGS,
			ACCOUNT_TYPE_RETIREMENT, ACCOUNT_TYPE_YOUTH };
}
