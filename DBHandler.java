package dbHandler;
import db.ConnectToDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DBHandler {

	private ConnectToDB conDB;
	private Statement stmt = null;
	private ResultSet res = null;
	private String dbURL;
	private String dbName;
	private String userName;
	private String password;


	public DBHandler(String dbURL, String dbName, String userName, String password) throws SQLException{

		this.dbURL = dbURL;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	private void connectStmt() throws SQLException {
		try {
			conDB = new ConnectToDB(this.dbURL, this.dbName, this.userName, this.password);

		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			stmt = conDB.getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close() {
		if (res != null)
		{
			try {
				res.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}

		}
		if (stmt != null)
		{
			try {
				stmt.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}

		}
		conDB.closeConnection();
	}

	public void showTable(String tablename) throws SQLException {

		connectStmt();
		String sqlrs = "SELECT * FROM " + tablename;
		res = stmt.executeQuery(sqlrs);

		GetLineLength(res);
		System.out.print("| " + tablename);
		for (int i = 1; i <= (GetCountTableLength(res) - tablename.length() - 1); i++) {
			System.out.print(" ");
		}
		System.out.println("|");
		GetLineLength(res);
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
			if  (res.getMetaData().getColumnDisplaySize(i) < res.getMetaData().getColumnName(i).length()) {
				System.out.printf("%-" + ((res.getMetaData().getColumnName(i).length() + 4)) + "s", "| " + res.getMetaData().getColumnName(i));
			}else {			
				System.out.printf("%-" + (res.getMetaData().getColumnDisplaySize(i) + 2) + "s", "| " + res.getMetaData().getColumnName(i));
			}
		}
		System.out.println(" |");
		GetLineLength(res);

		while (res.next()) {
			for(int i = 1; i <= res.getMetaData().getColumnCount(); i++) {

				if (res.getMetaData().getColumnDisplaySize(i) < res.getMetaData().getColumnName(i).length()) {
					System.out.printf("%-" + (res.getMetaData().getColumnName(i).length() + 4) + "s", "| " + res.getString(i));
				}else {			
					System.out.printf("%-" + (res.getMetaData().getColumnDisplaySize(i) + 2) + "s", "| " + res.getString(i));
				}
			}
			System.out.println(" |");
		}
		GetLineLength(res);
		close();
	}

	public void copyFile(String filename, String tablename) throws SQLException, FileNotFoundException{

		connectStmt();	
		Scanner input1 = null;
		int countOrd = 0;
		ArrayList<String> list1 = new ArrayList<String>();		
		String lists[][];	

		input1 = new Scanner(new File(filename));

		while (input1.hasNext()) {
			String ordet = input1.next();
			if (countOrd != 0 || ordet.equals("/")) {

			}

			else
				list1.add(ordet.replace("/", " "));

			if (ordet.contains("/")) {
				countOrd++;
			}

		}
		input1.close();
		input1 = new Scanner(new File(filename));

		lists = new String[countOrd][list1.size()];
		for (int i = 0; i < countOrd; i++) {
			for (int j = 0; j < list1.size(); j++) {
				if ((input1.hasNext() == false) && (i > 2))
					lists[i][j] = null;
				else {
					String ord = input1.next();
					if (ord.equals("/")) {
						j--;
					} else {						
						lists[i][j] = ord.replace("/", "");
					}
				}

			}				
		}

		input1.close();		
		try {
			stmt.executeUpdate("DROP TABLE " + tablename);
		} catch (Exception e) {
			System.out.println("Table dosent eksist");
		}

		String sqlCreate = "CREATE TABLE Test1 (";
		for (int i = 0; i < list1.size(); i++) {
			sqlCreate += lists[0][i] + " " + lists[1][i] + "(" + lists[2][i] + ")";
			if (i != list1.size() - 1)
				sqlCreate += ",";
		}
		sqlCreate += ");";

		stmt.execute(sqlCreate);

		String sqlInsert = null;
		for (int i = 3; i < countOrd; i++) {
			sqlInsert = "INSERT INTO Test1 " + "VALUES (";
			for (int j = 0; j < list1.size(); j++){
				sqlInsert += "'" + lists[i][j] + "'";	
				if (j != list1.size() - 1)
					sqlInsert += ",";
			}
			sqlInsert += ");";
			stmt.executeUpdate(sqlInsert);
		}
		close();


	}
	public static void GetLineLength(ResultSet res) throws SQLException {
		int count = GetCountTableLength(res);
		System.out.print("+-");
		count -= 2;
		for (int i = 1; i <= count; i++) {
			System.out.print("-" );
		}
		System.out.println("-+");
	}

	private static int GetCountTableLength(ResultSet res) throws SQLException {
		int countTabelLength = 0;
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
			if  (res.getMetaData().getColumnDisplaySize(i) < res.getMetaData().getColumnName(i).length()) {
				countTabelLength += (res.getMetaData().getColumnName(i).length() + 4);
			}else {			
				countTabelLength += (res.getMetaData().getColumnDisplaySize(i) + 2);
			}
		}
		return countTabelLength;	
	}



}
