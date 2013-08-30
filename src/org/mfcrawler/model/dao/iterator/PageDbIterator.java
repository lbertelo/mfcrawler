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

package org.mfcrawler.model.dao.iterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.site.Page;

/**
 * A iterator DAO for page
 * 
 * @author lbertelo
 */
public class PageDbIterator extends DbIterator {

	/**
	 * Indicates if the iterator return the page with or without its content
	 */
	private boolean withContent;

	/**
	 * Default constructor
	 */
	public PageDbIterator() {
		super();
	}

	/**
	 * Constructor with a resultSet
	 * @param resultSet the resultSet
	 * @param withContent indicates if the page is returned with the content
	 */
	public PageDbIterator(ResultSet resultSet, boolean withContent) {
		super(resultSet);
		this.withContent = withContent;
	}

	/**
	 * Return the next page and move the cursor
	 * @return the page
	 */
	public Page next() {
		Page page = null;
		try {
			if (hasNext()) {
				if (withContent) {
					page = PageDAO.toPageWithContent(getResultSet());
				} else {
					page = PageDAO.toPageWithoutContent(getResultSet());
				}

				setHasNext(getResultSet().next());
				if (!hasNext()) {
					getResultSet().close();
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDbIterator.class.getName()).log(Level.SEVERE, "Error to get next pageDb iterator", e);
		}
		return page;
	}

}
