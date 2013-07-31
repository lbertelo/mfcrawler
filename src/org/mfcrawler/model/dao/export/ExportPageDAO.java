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

package org.mfcrawler.model.dao.export;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.dao.BaseDAO;
import org.mfcrawler.model.dao.JdbcTools;
import org.mfcrawler.model.dao.iterator.LinkDbIterator;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.dao.site.IPageQueryList;
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
			+ " IS NOT NULL ";

	/**
	 * Selects pages to export : extension for domains
	 */
	private static final String EXPORT_PAGE_LIST_DOMAINS = " AND " + DOMAIN + " IN ( ";

	/**
	 * Selects pages to export : extension for minimum score
	 */
	private static final String EXPORT_PAGE_LIST_MIN_SCORE = " AND " + SCORE + " >= ? ";

	/**
	 * "Order by" for query which selects pages to export
	 */
	private static final String EXPORT_PAGE_LIST_ORDER = " ORDER BY " + DOMAIN + " ASC, " + PATH + " ASC, " + PROTOCOL;

	/**
	 * Query which selects outgoing links to export
	 */
	private static final String EXPORT_EXTERN_LINKS_START = " SELECT " + LINK_DOMAIN + ", " + LINK_PATH + ", "
			+ LINK_PROTOCOL + " FROM " + TABLE_LINK + " WHERE " + DOMAIN + " = ? AND " + PATH + " =  ? AND " + PROTOCOL
			+ " = ? AND " + LINK_DOMAIN + " IN ( ";

	/**
	 * "Order by" for query which selects outgoing links to export
	 */
	private static final String EXPORT_EXTERN_LINKS_ORDER = " ORDER BY " + LINK_DOMAIN + " ASC, " + LINK_PATH
			+ " ASC, " + LINK_PROTOCOL;

	/**
	 * Query which selects source links
	 */
	private static final String EXPORT_SOURCE_LINKS = " SELECT " + PROTOCOL + ", " + DOMAIN + ", " + PATH + " FROM "
			+ TABLE_PAGE + " WHERE " + CONTENT + " IS NOT NULL AND " + SCORE + " >= ? ORDER BY " + DOMAIN + " ASC, "
			+ PATH + " ASC, " + PROTOCOL + " ASC ";

	/**
	 * Query which selects target links
	 */
	private static final String EXPORT_TARGET_LINKS = " SELECT " + TABLE_LINK_P + LINK_PROTOCOL + ", " + TABLE_LINK_P
			+ LINK_DOMAIN + ", " + TABLE_LINK_P + LINK_PATH + " FROM " + TABLE_LINK + " INNER JOIN " + TABLE_PAGE
			+ " ON ( " + TABLE_PAGE_P + PROTOCOL + " = " + TABLE_LINK_P + LINK_PROTOCOL + " AND " + TABLE_PAGE_P
			+ DOMAIN + " = " + TABLE_LINK_P + LINK_DOMAIN + " AND " + TABLE_PAGE_P + PATH + " = " + TABLE_LINK_P
			+ LINK_PATH + " ) WHERE " + TABLE_PAGE_P + CONTENT + " IS NOT NULL AND " + TABLE_PAGE_P + SCORE
			+ " >= ? AND " + TABLE_LINK_P + PROTOCOL + " = ? AND " + TABLE_LINK_P + DOMAIN + " = ? AND " + TABLE_LINK_P
			+ PATH + " = ? ORDER BY " + TABLE_LINK_P + LINK_DOMAIN + " ASC, " + TABLE_LINK_P + LINK_PATH + " ASC, "
			+ TABLE_LINK_P + LINK_PROTOCOL + " ASC ";

	// Pages to export

	/**
	 * Select pages to export for domains passed in parameter and return an
	 * iterator
	 * @param domainNodesList domains which are selected to export
	 * @return the page iterator
	 */
	public PageDbIterator getPageListToExport(List<Domain> domainNodesList) {
		PageDbIterator pageIterator = new PageDbIterator();
		StringBuilder sql = new StringBuilder(EXPORT_PAGE_LIST_START);
		sql.append(EXPORT_PAGE_LIST_DOMAINS);

		int nodesSetSize = domainNodesList.size();
		if (nodesSetSize > 0) {
			sql.append("?");
			for (int i = 1; i < nodesSetSize; i++) {
				sql.append(", ?");
			}
		}
		sql.append(" ) ");
		sql.append(EXPORT_PAGE_LIST_ORDER);

		try {
			PreparedStatement preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			for (Domain domain : domainNodesList) {
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
	 * Select pages to export, with a minimum score and return an iterator
	 * @param minScore the minimum score
	 * @return the page iterator
	 */
	public PageDbIterator getPageListToExport(Double minScore) {
		PageDbIterator pageIterator = new PageDbIterator();
		StringBuilder sql = new StringBuilder(EXPORT_PAGE_LIST_START);
		sql.append(EXPORT_PAGE_LIST_MIN_SCORE);
		sql.append(EXPORT_PAGE_LIST_ORDER);

		try {
			PreparedStatement preStatement = connection.prepareStatement(sql.toString());
			JdbcTools.setDouble(preStatement, 1, minScore);
			ResultSet result = preStatement.executeQuery();
			pageIterator = new PageDbIterator(result);
		} catch (SQLException e) {
			Logger.getLogger(ExportPageDAO.class.getName()).log(Level.SEVERE, "Error to get page list to export", e);
		}

		return pageIterator;
	}

	// Links to export

	/**
	 * Select outgoing extern links from a page and a list of domains
	 * @param page the page
	 * @param domainNodesList domains which are selected to export
	 * @return the list of links
	 */
	public List<Link> getOutgoingExternLinks(Page page, List<Domain> domainNodesList) {
		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Link> linkList = new ArrayList<Link>();

		StringBuilder sql = new StringBuilder(EXPORT_EXTERN_LINKS_START);
		int numberOfDomains = domainNodesList.size();
		if (numberOfDomains > 0) {
			sql.append("?");
			for (int i = 1; i < numberOfDomains; i++) {
				sql.append(", ?");
			}
		}
		sql.append(" ) ");
		sql.append(EXPORT_EXTERN_LINKS_ORDER);

		try {
			preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			JdbcTools.setString(preStatement, i++, page.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, i++, page.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, i++, page.getLink().getLinkPath().getProtocol());
			for (Domain domain : domainNodesList) {
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

	/**
	 * Select source links from crawled pages with a minimum score
	 * @param minScore the minimum score
	 * @return the link iterator
	 */
	public LinkDbIterator getSourceLinkList(Double minScore) {
		LinkDbIterator linkIterator = new LinkDbIterator();

		try {
			PreparedStatement preStatement = connection.prepareStatement(EXPORT_SOURCE_LINKS);
			JdbcTools.setDouble(preStatement, 1, minScore);
			ResultSet result = preStatement.executeQuery();
			linkIterator = new LinkDbIterator(result);
		} catch (SQLException e) {
			Logger.getLogger(ExportPageDAO.class.getName()).log(Level.SEVERE, "Error to get source links to export", e);
		}

		return linkIterator;
	}

	/**
	 * Select target links from a source link and a minimum score
	 * @param sourceLink the source link
	 * @param minScore the minimum score for a page
	 * @return the list of links
	 */
	public List<Link> getTargetLinkList(Link sourceLink, Double minScore) {
		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Link> linkList = new ArrayList<Link>();

		try {
			preStatement = connection.prepareStatement(EXPORT_TARGET_LINKS);
			JdbcTools.setDouble(preStatement, 1, minScore);
			JdbcTools.setString(preStatement, 2, sourceLink.getLinkPath().getProtocol());
			JdbcTools.setString(preStatement, 3, sourceLink.getDomain().getName());
			JdbcTools.setString(preStatement, 4, sourceLink.getLinkPath().getPath());

			result = preStatement.executeQuery();
			while (result.next()) {
				Link link = new Link(JdbcTools.getString(result, LINK_PROTOCOL), JdbcTools.getString(result,
						LINK_DOMAIN), JdbcTools.getString(result, LINK_PATH));
				linkList.add(link);
			}
		} catch (SQLException e) {
			Logger.getLogger(ExportPageDAO.class.getName()).log(Level.SEVERE, "Error to get target links to export", e);
		} finally {
			close(result, preStatement);
		}

		return linkList;
	}

}
