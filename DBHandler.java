package dbHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import db.ConnectToDB;

public class DBHandler {

	private ConnectToDB conDB;
	private Statement stmt = null;
	private ResultSet res = null;

	public DBHandler(String dbURL, String dbName, String userName, String password) throws SQLException{

		// resends connection info to ConnectToDB
		conDB = new ConnectToDB(dbURL, dbName, userName, password);

	}
	// connects to db and set'sup statement
	private void connectStmt() throws SQLException {
		try {
			conDB.getConnection();
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			stmt = conDB.getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Closes ResultSet, Statement and send close request method to ConnectToDB
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



	// Gets input from file and creates a table based on the file
	public void copyFile(String filename, String tablename) throws SQLException, FileNotFoundException{

		connectStmt();	
		Scanner inputFile = null;
		int countSlash = 0;
		ArrayList<String> firstLineList = new ArrayList<String>();		
		String lists[][];	

		// scann's file
		inputFile = new Scanner(new File(filename));

		// cheking for a /, and counting to use later 
		while (inputFile.hasNext()) {
			String word = inputFile.next();
			if (countSlash != 0 || word.equals("/")) {

			}

			else
				firstLineList.add(word.replace("/", " "));

			if (word.contains("/")) {
				countSlash++;
			}
		}
		//closes input1.
		inputFile.close();

		// Re open input1 for reRead
		inputFile = new Scanner(new File(filename));

		//Declares a multidimensional  array, ads words form file to separate places in the array.
		lists = new String[countSlash][firstLineList.size()];
		for (int rows = 0; rows < countSlash; rows++) {
			for (int columns = 0; columns < firstLineList.size(); columns++) {
				// the tree first slashed lines is added
				if ((inputFile.hasNext() == false) && (rows > 2))
					lists[rows][columns] = null;
				// the rest of the lines is added
				else {
					String slashChar = inputFile.next();
					if (slashChar.equals("/")) {
						columns--;
					} else {						
						lists[rows][columns] = slashChar.replace("/", "");
					}
				}

			}				
		}
		// close the file
		inputFile.close();	

		// if exists drop table
		try {
			stmt.executeUpdate("DROP TABLE " + tablename);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// creates a new table and columns names, data types and width
		String sqlCreate = "CREATE TABLE " + tablename + "(";
		for (int columns = 0; columns < firstLineList.size(); columns++) {
			sqlCreate += lists[0][columns] + " " + lists[1][columns] + "(" + lists[2][columns] + ")";
			if (columns != firstLineList.size() - 1)
				sqlCreate += ",";
		}
		sqlCreate += ");";

		stmt.execute(sqlCreate);

		// adds the table values
		String sqlInsert = null;
		for (int rows = 3; rows < countSlash; rows++) {
			sqlInsert = "INSERT INTO "+ tablename + " VALUES (";
			for (int columns = 0; columns < firstLineList.size(); columns++){
				sqlInsert += "'" + lists[rows][columns] + "'";	
				if (columns != firstLineList.size() - 1)
					sqlInsert += ",";
			}
			sqlInsert += ");";
			stmt.executeUpdate(sqlInsert);
		}
		close();
	}

	// showTable will show a table in terminal
	public void showTable(String tablename) throws SQLException {
		//connects DataBase
		connectStmt();

		// runs sql request
		String sqlrs = "SELECT * FROM " + tablename;
		res = stmt.executeQuery(sqlrs);

		// prints table name and ads a frame to console
		GetLineLength(res);
		System.out.print("| " + tablename);
		for (int i = 1; i <= (GetCountTableLength(res) - tablename.length() - 1); i++) {
			System.out.print(" ");
		}
		System.out.println("|");
		GetLineLength(res);

		// ads all collum names and sets a frame to console
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
			if  (res.getMetaData().getColumnDisplaySize(i) < res.getMetaData().getColumnName(i).length()) {
				System.out.printf("%-" + ((res.getMetaData().getColumnName(i).length() + 4)) + "s", "| " + res.getMetaData().getColumnName(i));
			}else {			
				System.out.printf("%-" + (res.getMetaData().getColumnDisplaySize(i) + 2) + "s", "| " + res.getMetaData().getColumnName(i));
			}
		}
		System.out.println(" |");
		GetLineLength(res);

		// prints out all the info from the DataBase
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

	// crates correct size lines in the console box
	public static void GetLineLength(ResultSet res) throws SQLException {
		int count = GetCountTableLength(res);
		System.out.print("+-");
		count -= 2;
		for (int i = 1; i <= count; i++) {
			System.out.print("-" );
		}
		System.out.println("-+");
	}

	//counts the length of the table box in console
	private static int GetCountTableLength(ResultSet res) throws SQLException {
		int countTabelLength = 0;
		for (int columen = 1; columen <= res.getMetaData().getColumnCount(); columen++) {
			if  (res.getMetaData().getColumnDisplaySize(columen) < res.getMetaData().getColumnName(columen).length()) {
				countTabelLength += (res.getMetaData().getColumnName(columen).length() + 4);
			}else {			
				countTabelLength += (res.getMetaData().getColumnDisplaySize(columen) + 2);
			}
		}
		return countTabelLength;	
	}



}
