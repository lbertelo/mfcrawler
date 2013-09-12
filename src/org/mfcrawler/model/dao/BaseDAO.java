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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Super class for all DAO classes
 * 
 * @author lbertelo
 */
public class BaseDAO {

	/**
	 * Map containing a boolean which shows if errors have occurred for a
	 * transaction (depending on the connection)
	 */
	private static Map<Connection, Boolean> errorConnectionMap = new HashMap<Connection, Boolean>();

	/**
	 * A connection to the database
	 */
	protected Connection connection;

	/**
	 * Default constructor which uses the main database connection
	 */
	public BaseDAO() {
		connection = DbmsManager.get().getMainConnection();
	}

	/**
	 * Constructor which uses the connection passed in parameter
	 * @param connection connection used
	 */
	public BaseDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Begin transaction, set the autoCommit to false and initialize the
	 * variable which shows if errors have occurred
	 */
	public void beginTransaction() {
		try {
			errorConnectionMap.put(connection, Boolean.FALSE);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			Logger.getLogger(BaseDAO.class.getName()).log(Level.SEVERE, "Error to begin transaction", e);
		}
	}

	/**
	 * End transaction, commit or rollback the transaction if errors have
	 * occurred then set the autoCommit to true
	 */
	public void endTransaction() {
		try {
			Boolean error = errorConnectionMap.get(connection);
			if (error != null && error.equals(Boolean.FALSE)) {
				connection.commit();
			} else {
				Logger.getLogger(BaseDAO.class.getName()).log(Level.WARNING, "Rollback transaction");
				connection.rollback();
			}
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			Logger.getLogger(BaseDAO.class.getName()).log(Level.SEVERE, "Error to end transaction", e);
		}
	}

	/**
	 * Indicates that a error occurred during a transaction
	 */
	protected void errorOccurred() {
		errorConnectionMap.put(connection, Boolean.TRUE);
	}

	/**
	 * Close the statement passed in parameter (catch SQL Exception)
	 * @param statement the statement
	 */
	public void close(Statement statement) {
		try {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(BaseDAO.class.getName()).log(Level.SEVERE, "Error to close statement", e);
		}
	}

	/**
	 * Close the resultSet then the statement passed in parameter (catch SQL
	 * Exception)
	 * @param resultSet the resultSet
	 * @param statement the statement
	 */
	public void close(ResultSet resultSet, Statement statement) {
		try {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(BaseDAO.class.getName()).log(Level.SEVERE, "Error to close resultSet", e);
		}
		close(statement);
	}

}
