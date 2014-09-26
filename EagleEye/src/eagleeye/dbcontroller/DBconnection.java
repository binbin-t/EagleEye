package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {

	public static Connection dbConnector()
	{
		Connection dbConn = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			dbConn = DriverManager.getConnection("jdbc:sqlite:DBresources\\ForensicsSuiteDB.sqlite");
			return dbConn;
			
		} catch (Exception e) {
			
			System.out.println("Connection fail");
			return dbConn;
		}
	}
}