package Controller;

import java.sql.ResultSet;

public abstract class LoginHandler {

	public static int CheckLogin(String Username, String Password) {
		if (Username.equals("Employee Manager") && Password.equals("manager")) {
			return -2;
		}

		if (Username.equals("Loan Manager") && Password.equals("manager")) {
			return -3;
		}

		if (Username.equals("Regional Manager") && Password.equals("manager")) {
			return -4;
		}

		try {
			ResultSet set = DatabaseHandler.Select("SELECT * FROM EMPLOYEE WHERE NAME ='" + Username + "' AND POSID <> 1");

			while (set.next()) {
				String pass = set.getString(3).substring(0, 3) + set.getString(5).substring(0, 3);
				if (pass.equals(Password))
					return set.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

}
