package org.mfcrawler.model.dao.iterator;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mfcrawler.model.dao.ITablesVocabulary;

/**
 * Abstract class for DAO iterator
 * 
 * @author lbertelo
 */
public abstract class DbIterator implements ITablesVocabulary {

	/**
	 * The resultSet of the iterator
	 */
	private ResultSet resultSet;

	/**
	 * Indicates if the iterator has a next value
	 */
	private boolean hasNext;
	
	/**
	 * Default constructor
	 */
	protected DbIterator() {
		resultSet = null;
		hasNext = false;
	}
	
	/**
	 * Constructor with a resultSet
	 * @param resultSet the resultSet
	 */
	protected DbIterator(ResultSet resultSet) {
		this.resultSet = resultSet;
		try {
			hasNext = resultSet.next();
		} catch (SQLException e) {
			hasNext = false;
		}
	}
	
	/**
	 * Indicates if iterator has a next page
	 * @return true if has, false otherwise
	 */
	public boolean hasNext() {
		return hasNext;
	}
	
	/**
	 * Protected Setter of hasNext
	 * @param hasNext the boolean hasNext
	 */
	protected void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	
	/**
	 * Protected Getter of resultSet
	 * @return the resultSet
	 */
	protected ResultSet getResultSet() {
		return resultSet;
	}
}
