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

import org.mfcrawler.model.dao.JdbcTools;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * A iterator DAO for link
 * 
 * @author lbertelo
 */
public class LinkDbIterator extends DbIterator {

	/**
	 * Default constructor
	 */
	public LinkDbIterator() {
		super();
	}
	
	/**
	 * Constructor with a resultSet
	 * @param resultSet the resultSet
	 */
	public LinkDbIterator(ResultSet resultSet) {
		super(resultSet);
	}

	/**
	 * Return the next link and move the cursor
	 * @return the link
	 */
	public Link next() {
		Link link = null;
		try {
			if (hasNext()) {
				link = new Link(JdbcTools.getString(getResultSet(), PROTOCOL), JdbcTools.getString(getResultSet(),
						DOMAIN), JdbcTools.getString(getResultSet(), PATH));
				setHasNext(getResultSet().next());
				if (!hasNext()) {
					getResultSet().close();
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(LinkDbIterator.class.getName()).log(Level.SEVERE, "Error to get next linkDb iterator", e);
		}
		return link;
	}

}
