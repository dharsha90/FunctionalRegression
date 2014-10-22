package com.Thd.Regression;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DbConnection {
	private static String DB_DRIVER,DB_CONNECTION_URL,DB_USER,DB_PASSWORD;
	private static Connection dbConnection=null;
	public static Connection getDBConnection() throws IOException
	{/*
		Properties DBProperty = new Properties();
		DBProperty.load(DbConnection.class.getResourceAsStream("/config.properties"));
		DB_DRIVER = DBProperty.getProperty("DB_Driver");
		DB_CONNECTION_URL = DBProperty.getProperty("DB_Connection_URL");
		DB_USER = DBProperty.getProperty("DB_User");
		DB_PASSWORD = DBProperty.getProperty("DB_Password");
		if (dbConnection == null) {
			try {
				Class.forName(DB_DRIVER);
				dbConnection = DriverManager.getConnection(DB_CONNECTION_URL,DB_USER, DB_PASSWORD);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}*/
		return dbConnection;
		
	}
	
	/*public static void main(String[] args) throws IOException
	{
		System.out.println(getDBConnection());
	}*/
	
	

}
