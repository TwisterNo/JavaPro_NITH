package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;

import dbHandler.*;

public class Main {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws SQLException, FileNotFoundException {
		// gets connect info from args
		String dbURL = args[0];
		String dbName = args[1];
		String userName = args[2];
		String password = args[3];
		DBHandler db = new DBHandler(dbURL, dbName, userName, password);
		
		// creates a test file to make table of.
		@SuppressWarnings("resource")
		PrintStream output = new PrintStream(new File("cars.txt"));
		
		output.println("SerialNr Type Color Price/");
		output.println("Int varchar varchar Int/");
		output.println("6 10 10 6/");
		output.println("011111 Civic Blue 350000/");
		output.println("011112 Accord Black 450000 /");
		output.print("011113 Jazz Null 220000/");
		
		
		// runs method to store file info in to database
		db.copyFile("cars.txt", "CarsList");

		// runs method to show Table in console
		db.showTable("CarsList");		
	}

}
