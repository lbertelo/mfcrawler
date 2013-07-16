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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tools for Jdbc which get form resultSet and set to preparedStatement Test if
 * the values are null
 * 
 * @author lbertelo
 */
public class JdbcTools {

	/**
	 * Length of buffer for read Clob and Blob
	 */
	private static final int BUFFER_LENGTH = 2048;

	// Getter to ResultSet

	/**
	 * Get String from resultSet
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a String or null
	 * @throws SQLException
	 */
	public static String getString(ResultSet result, String columnLabel) throws SQLException {
		String value = result.getString(columnLabel);
		if (result.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Get Integer from resultSet
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a Integer or null
	 * @throws SQLException
	 */
	public static Integer getInteger(ResultSet result, String columnLabel) throws SQLException {
		int value = result.getInt(columnLabel);
		if (result.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Get Double from resultSet
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a Double or null
	 * @throws SQLException
	 */
	public static Double getDouble(ResultSet result, String columnLabel) throws SQLException {
		double value = result.getDouble(columnLabel);
		if (result.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Get Boolean from resultSet
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a Boolean or null
	 * @throws SQLException
	 */
	public static Boolean getBoolean(ResultSet result, String columnLabel) throws SQLException {
		boolean value = result.getBoolean(columnLabel);
		if (result.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Get Date from resultSet
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a Date or null
	 * @throws SQLException
	 */
	public static Date getDate(ResultSet result, String columnLabel) throws SQLException {
		Date value = result.getTimestamp(columnLabel);
		if (result.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Get String from resultSet (read Clob)
	 * @param result the resultSet
	 * @param columnLabel the column label
	 * @return a String or null
	 * @throws SQLException
	 */
	public static String getClob(ResultSet result, String columnLabel) throws SQLException {
		StringBuilder stringBuilder = new StringBuilder();
		Clob clob = result.getClob(columnLabel);

		if (result.wasNull()) {
			return null;
		} else {
			try {
				Reader reader = clob.getCharacterStream();
				int length = (int) clob.length();

				if (length > 0) {
					char buffer[] = new char[BUFFER_LENGTH];
					int readSize;

					while ((readSize = reader.read(buffer)) > 0) {
						stringBuilder.append(buffer, 0, readSize);
					}

					reader.close();
				}
			} catch (IOException e) {
				Logger.getLogger(JdbcTools.class.getName()).log(Level.SEVERE, "Error to get Clob", e);
			}

			return stringBuilder.toString();
		}
	}

	// Setter to PreparedStatement

	/**
	 * Set a Varchar in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a String or null
	 * @throws SQLException
	 */
	public static void setString(PreparedStatement preStatement, int parameterIndex, String value) throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.VARCHAR);
		} else {
			preStatement.setString(parameterIndex, value);
		}
	}

	/**
	 * Set a Integer in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a Integer or null
	 * @throws SQLException
	 */
	public static void setInteger(PreparedStatement preStatement, int parameterIndex, Integer value)
			throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.INTEGER);
		} else {
			preStatement.setInt(parameterIndex, value);
		}
	}

	/**
	 * Set a Double in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a Double or null
	 * @throws SQLException
	 */
	public static void setDouble(PreparedStatement preStatement, int parameterIndex, Double value) throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.INTEGER);
		} else {
			preStatement.setDouble(parameterIndex, value);
		}
	}

	/**
	 * Set a Boolean in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a Boolean or null
	 * @throws SQLException
	 */
	public static void setBoolean(PreparedStatement preStatement, int parameterIndex, Boolean value)
			throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.BOOLEAN);
		} else {
			preStatement.setBoolean(parameterIndex, value);
		}
	}

	/**
	 * Set a Date in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a Date or null
	 * @throws SQLException
	 */
	public static void setDate(PreparedStatement preStatement, int parameterIndex, Date value) throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.DATE);
		} else {
			preStatement.setTimestamp(parameterIndex, new Timestamp(value.getTime()));
		}
	}

	/**
	 * Set a Clob in the prepared statement
	 * @param preStatement the prepared statement
	 * @param parameterIndex the parameter index
	 * @param value a String or null
	 * @throws SQLException
	 */
	public static void setClob(PreparedStatement preStatement, int parameterIndex, String value) throws SQLException {
		if (value == null) {
			preStatement.setNull(parameterIndex, Types.CLOB);
		} else {
			Reader reader = new StringReader(value);
			preStatement.setClob(parameterIndex, reader);
		}
	}

}
