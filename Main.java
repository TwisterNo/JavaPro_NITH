package main;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import dbHandler.*;

public class Main {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		DBHandler db = new DBHandler("jdbc:mysql://localhost", "test", "root", "******");
		
		try {
			db.copyFile("test.txt", "Test1");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.showTable("Test1");

		
	}

}
