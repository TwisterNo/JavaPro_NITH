package db;

import java.sql.*;

public class ConnectToDB {
	// ads the variables
	private String userName;
	private String password;
	private String dbURL;
	private String dbName;
	private Connection con = null;

	//gets the DataBase connection info 
	public ConnectToDB( String dbURL, String dbName, String userName, String password) throws SQLException {
		this.dbURL = dbURL;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	// Closes the connection to the database
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

	// Opens connection to database
	public Connection getConnection() throws SQLException {
		this.con = DriverManager.getConnection(this.dbURL + "/" + this.dbName, this.userName, this.password);
		return this.con;

	}

}
