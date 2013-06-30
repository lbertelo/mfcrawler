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

package org.mfcrawler.model.export;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.dao.BaseDAO;
import org.mfcrawler.model.dao.JdbcTools;
import org.mfcrawler.model.dao.site.IPageQueryList;
import org.mfcrawler.model.dao.site.PageDbIterator;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * DAO for Pages, specific to export
 * 
 * @author lbertelo
 */
public class ExportPageDAO extends BaseDAO implements IPageQueryList {

	/**
	 * Query which selects pages to export
	 */
	private static final String EXPORT_PAGE_LIST_START = " SELECT * FROM " + TABLE_PAGE + " WHERE " + CONTENT
			+ " IS NOT NULL " + " AND " + DOMAIN + " IN ( ";

	/**
	 * Query which selects links to export
	 */
	private static final String EXPORT_EXTERN_LINKS_START = " SELECT * FROM " + TABLE_LINK + " WHERE " + DOMAIN
			+ " = ? AND " + PATH + " =  ? AND " + PROTOCOL + " = ? AND " + LINK_DOMAIN + " IN ( ";

	/**
	 * Select pages to export for domains passed in parameter and return an
	 * iterator
	 * @param domainNodesSet domains which are selected to export
	 * @return the page iterator or null
	 */
	public PageDbIterator getPageListToExport(Set<Domain> domainNodesSet) {
		PageDbIterator pageIterator = null;
		StringBuilder sql = new StringBuilder(EXPORT_PAGE_LIST_START);

		int nodesSetSize = domainNodesSet.size();
		if (nodesSetSize > 0) {
			sql.append("?");
			for (int i = 1; i < nodesSetSize; i++) {
				sql.append(", ?");
			}
		}
		sql.append(" ) ");

		try {
			PreparedStatement preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			for (Domain domain : domainNodesSet) {
				JdbcTools.setString(preStatement, i++, domain.getName());
			}

			ResultSet result = preStatement.executeQuery();
			pageIterator = new PageDbIterator(result);
		} catch (SQLException e) {
			Logger.getLogger(ExportPageDAO.class.getName()).log(Level.SEVERE, "Error to get page list to export", e);
		}

		return pageIterator;
	}

	/**
	 * Select all links from a page to a set of domains
	 * @param page the page
	 * @param domainNodesSet domains which are selected to export
	 * @return the list of links
	 */
	public List<Link> getExternLinksToExport(Page page, Set<Domain> domainNodesSet) {
		StringBuilder sql = new StringBuilder(EXPORT_EXTERN_LINKS_START);

		int nodesSetSize = domainNodesSet.size();
		if (nodesSetSize > 0) {
			sql.append("?");
			for (int i = 1; i < nodesSetSize; i++) {
				sql.append(", ?");
			}
		}
		sql.append(" ) ");

		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Link> linkList = new ArrayList<Link>();
		try {
			preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			JdbcTools.setString(preStatement, i++, page.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, i++, page.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, i++, page.getLink().getLinkPath().getProtocol());
			for (Domain domain : domainNodesSet) {
				JdbcTools.setString(preStatement, i++, domain.getName());
			}

			result = preStatement.executeQuery();
			while (result.next()) {
				Link link = new Link(JdbcTools.getString(result, LINK_PROTOCOL), JdbcTools.getString(result,
						LINK_DOMAIN), JdbcTools.getString(result, LINK_PATH));
				linkList.add(link);
			}
		} catch (SQLException e) {
			Logger.getLogger(ExportPageDAO.class.getName()).log(Level.SEVERE, "Error to get extern links to export", e);
		} finally {
			close(result, preStatement);
		}

		return linkList;
	}
}
