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

package org.mfcrawler.model.dao.site;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.pojo.site.Page;

/**
 * A iterator DAO for page
 * 
 * @author lbertelo
 */
public class PageDbIterator {

	/**
	 * The resultSet of the iterator
	 */
	private ResultSet resultSet;

	/**
	 * Indicates if the iterator has a next value
	 */
	private boolean hasNext;

	/**
	 * Constructor
	 * @param resultSet the resultSet
	 */
	public PageDbIterator(ResultSet resultSet) {
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
	 * Return the next page and move the cursor
	 * @return the page
	 */
	public Page next() {
		Page page = null;
		try {
			if (hasNext) {
				page = PageDAO.toPageWithoutContent(resultSet);
				hasNext = resultSet.next();
				if (!hasNext) {
					resultSet.close();
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDbIterator.class.getName()).log(Level.SEVERE, "Error to get next pageDb iterator", e);
		}
		return page;
	}

}
