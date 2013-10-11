/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013  lbertelo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mfcrawler.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.export.config.LoadCrawlProjectConfig;

/**
 * Handles the Database Management System
 * 
 * @author lbertelo
 */
public final class DbmsManager {

	/**
	 * JDBC Driver
	 */
	private static final String JDBC_DRIVER = "org.h2.Driver";

	/**
	 * JDBC Protocol
	 */
	private static final String PROTOCOL = "jdbc:h2:";

	/**
	 * Database User
	 */
	private static final String USER = "sa";

	/**
	 * Database Password
	 */
	private static final String PASSWD = "";

	/**
	 * Time out allowed for a database connection
	 */
	private static final int VALID_CONNECTION_TIME_OUT = 5000;

	/**
	 * Singleton instance
	 */
	private static DbmsManager instance;

	/**
	 * Database name
	 */
	private String dbname;

	/**
	 * Main connection
	 */
	private Connection mainConnection;

	/**
	 * Pool of connections
	 */
	private JdbcConnectionPool connectionPool;

	/**
	 * Private constructor for Singleton, fetch the JDBC Driver
	 */
	private DbmsManager() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (Exception e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to get jdbc driver", e);
		}
	}

	/**
	 * Return the unique instance of DbmsManager
	 * @return instance of DbmsManager
	 */
	public synchronized static DbmsManager get() {
		if (instance == null) {
			instance = new DbmsManager();
		}
		return instance;
	}

	/**
	 * Connect to the database, initialize the connectionPool and the
	 * mainConnection
	 * @param projectName the projectName determines the name of database
	 */
	public void connect(String projectName) {
		dbname = LoadCrawlProjectConfig.getCompleteFilename(projectName, "h2Database");
		String dbOptions = ";CACHE_SIZE=128000";
		try {
			connectionPool = JdbcConnectionPool.create(PROTOCOL + dbname + dbOptions, USER, PASSWD);
			mainConnection = connectionPool.getConnection();
		} catch (SQLException e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to connect to DB", e);
		}
	}

	/**
	 * Initializes the DbmsManager by checking and creating tables, and testing
	 * the connection
	 */
	public void init() {
		if (!checkTables()) {
			createTables();
		}
		testConnection();
	}

	/**
	 * Test if the connection is ready and initialize the DBMS
	 * @return true if the test succeed, false otherwise
	 */
	private boolean testConnection() {
		boolean testConnect = false;
		try {
			if (mainConnection != null && mainConnection.isValid(VALID_CONNECTION_TIME_OUT)) {
				PreparedStatement preStatement = mainConnection.prepareStatement(ITablesVocabulary.TEST_QUERY);
				ResultSet result = preStatement.executeQuery();
				while (result.next()) {
					result.getString(1);
				}

				testConnect = true;
			}
		} catch (SQLException e) {
			testConnect = false;
		}
		return testConnect;
	}

	/**
	 * Check if tables are created
	 * @return true if tables exist, false otherwise
	 */
	private boolean checkTables() {
		ResultSet result;
		boolean hasAResult = false;
		try {
			result = mainConnection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
			// result is supposed to contain the description of our tables
			hasAResult = result.next();
			result.close();
		} catch (SQLException e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to check tables", e);
		}
		return hasAResult;
	}

	/**
	 * Create all tables (Sites and Pages)
	 */
	private void createTables() {
		StringBuilder sqlCreateTables = new StringBuilder();
		sqlCreateTables.append(SiteDAO.getSqlTablesCreation());
		sqlCreateTables.append(PageDAO.getSqlTablesCreation());
		try {
			Statement statement = mainConnection.createStatement();
			String[] sqlTab = sqlCreateTables.toString().trim().split(";");
			for (int i = 0; i < sqlTab.length; i++) {
				statement.execute(sqlTab[i]);
			}
			statement.close();
		} catch (SQLException e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to create tables", e);
		}
	}

	/**
	 * Clears the tables by dropping then creating tables
	 */
	public void clearTables() {
		dropTables();
		createTables();
	}

	/**
	 * Drop all tables (Sites and Pages)
	 */
	private void dropTables() {
		StringBuilder sqlDropTables = new StringBuilder();
		sqlDropTables.append(SiteDAO.getSqlTablesDeletion());
		sqlDropTables.append(PageDAO.getSqlTablesDeletion());
		try {
			Statement statement = mainConnection.createStatement();
			String[] sqlTab = sqlDropTables.toString().trim().split(";");
			for (int i = 0; i < sqlTab.length; i++) {
				statement.execute(sqlTab[i]);
			}
			statement.close();
		} catch (SQLException e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to drop tables", e);
		}
	}

	/**
	 * Return the main connection
	 * @return the main connection
	 */
	public Connection getMainConnection() {
		return mainConnection;
	}

	/**
	 * Return a new connection, got from the connection pool
	 * @return a new connection
	 */
	public Connection getNewConnection() {
		Connection newConnection = null;
		if (connectionPool != null) {
			try {
				newConnection = connectionPool.getConnection();
			} catch (SQLException e) {
				Logger.getLogger(DbmsManager.class.getName()).log(Level.SEVERE, "Error to get new connection", e);
			}
		}
		return newConnection;
	}

	/**
	 * Disconnect from the database (close all the main connection and the
	 * connection pool)
	 */
	public void disconnect() {
		try {
			if (mainConnection != null && !mainConnection.isClosed()) {
				mainConnection.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(DbmsManager.class.getName()).log(Level.INFO, "Error to close main connection", e);
		}

		if (connectionPool != null) {
			connectionPool.dispose();
		}
	}

}
