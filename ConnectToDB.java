package db;


import java.sql.*;

public class ConnectToDB {

	String userName;
	String password;
	String dbURL;
	String dbName;

	Connection con = null;


	public ConnectToDB( String dbURL, String dbName, String userName, String password) throws SQLException {


		this.dbURL = dbURL;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;

		this.con = DriverManager.getConnection(dbURL + "/" + dbName, userName, password);
	}

	public void closeConnection() {
		if (con != null)
		{
			try {
				con.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public Connection getConnection() throws SQLException {

		return this.con;

	}

}
