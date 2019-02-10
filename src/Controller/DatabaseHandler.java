package Controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseHandler {

	private static Connection DBConnection = null;

	public static void Initialise(String Login, String Password) {
		try {
			if (DBConnection != null)
				DBConnection.close();
			DBConnection = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=Proceed", Login,
					Password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ResultSet Select(String query) {
		try {
			return DBConnection.prepareStatement(query).executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void Query(String query) {
		try {
			DBConnection.prepareStatement(query).executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void AddEmployee(String Query, int POSID, String Name, String Phone, String Email, Date DOB,
			int ShiftDuration, Date DateHired) {
		try {
			CallableStatement statement = DBConnection.prepareCall(Query);

			statement.setInt(1, POSID);
			statement.setString(2, Name);
			statement.setString(3, Phone);
			statement.setString(4, Email);
			statement.setDate(5, DOB);
			statement.setInt(6, ShiftDuration);
			statement.setDate(7, DateHired);

			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void Transfer(int DID1, int DID2, int Value) {
		try {
			CallableStatement callableStatement = DBConnection
					.prepareCall("{call Cash_Transaction_Debit(?,?,?)}");
			callableStatement.setInt(1, DID1);
			callableStatement.setInt(2, DID2);
			callableStatement.setInt(3, Value);
			
			callableStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EndConnection() {
		try {
			if (DBConnection != null)
				DBConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
