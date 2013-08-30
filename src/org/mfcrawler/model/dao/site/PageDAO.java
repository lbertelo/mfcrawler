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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.dao.BaseDAO;
import org.mfcrawler.model.dao.JdbcTools;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.pojo.site.link.LinkPath;
import org.mfcrawler.model.pojo.site.link.OutgoingLink;

/**
 * DAO for Pages
 * 
 * @author lbertelo
 */
public class PageDAO extends BaseDAO implements IPageQueryList {

	/**
	 * Default constructor which uses the main database connection
	 */
	public PageDAO() {
		super();
	}

	/**
	 * Constructor which uses the connection passed in parameter
	 * @param connection connection used
	 */
	public PageDAO(Connection connection) {
		super(connection);
	}

	// TABLES CREATION

	/**
	 * Return the sql queries which permits to create tables referring to pages
	 * @return the sql queries
	 */
	public static String getSqlTablesCreation() {
		return CREATE_TABLES;
	}

	/**
	 * Return the sql queries which permits to delete tables referring to pages
	 * @return the sql queries
	 */
	public static String getSqlTablesDeletion() {
		return DROP_TABLES;
	}

	// BASE

	/**
	 * Extract a page without the content from a resultSet
	 * @param result the resultSet
	 * @return the page
	 */
	public static Page toPageWithoutContent(ResultSet result) {
		Page page = null;

		try {
			Link linkPage = new Link(JdbcTools.getString(result, PROTOCOL), JdbcTools.getString(result, DOMAIN),
					JdbcTools.getString(result, PATH));
			page = new Page(linkPage);

			page.setScore(JdbcTools.getDouble(result, SCORE));
			page.setInnerDeep(JdbcTools.getInteger(result, INNER_DEEP));
			page.setOuterDeep(JdbcTools.getInteger(result, OUTER_DEEP));
			page.setCrawlTime(JdbcTools.getDate(result, CRAWL_TIME));
			page.setAllowCrawl(JdbcTools.getBoolean(result, ALLOW_CRAWL));
			page.setRedirectPage(JdbcTools.getBoolean(result, REDIRECT_PAGE));
			page.setCrawlNow(JdbcTools.getBoolean(result, CRAWL_NOW));
			page.setCrawlError(JdbcTools.getString(result, CRAWL_ERROR));
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to read page in DB", e);
		}

		return page;
	}

	/**
	 * Extract a page with the content from a resultSet
	 * @param result the resultSet
	 * @return the page
	 */
	public static Page toPageWithContent(ResultSet result) {
		Page page = toPageWithoutContent(result);

		if (page != null) {
			try {
				page.setContent(JdbcTools.getClob(result, CONTENT));
			} catch (SQLException e) {
				Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to read content in DB", e);
			}
		}

		return page;
	}

	/**
	 * Extract a link from a resultSet and add it to the page
	 * @param page the page
	 * @param result the resultSet
	 */
	private static void setLinks(Page page, ResultSet result) {
		try {
			Link base = new Link(JdbcTools.getString(result, PROTOCOL), JdbcTools.getString(result, DOMAIN),
					JdbcTools.getString(result, PATH));
			Link link = new Link(JdbcTools.getString(result, LINK_PROTOCOL), JdbcTools.getString(result, LINK_DOMAIN),
					JdbcTools.getString(result, LINK_PATH));

			if (page.getLink().equals(base)) {
				boolean crawled = (JdbcTools.getDate(result, CRAWL_TIME) != null);
				OutgoingLink outgoingLink = new OutgoingLink(link, crawled);
				if (page.getLink().getDomain().equals(link.getDomain())) {
					page.getOutgoingInternLinks().add(outgoingLink);
				} else {
					page.getOutgoingExternLinks().add(outgoingLink);
				}
			} else {
				if (page.getLink().getDomain().equals(base.getDomain())) {
					page.getIncomingInternLinks().add(base);
				} else {
					page.getIncomingExternLinks().add(base);
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to set links", e);
		}
	}

	// SELECT

	/**
	 * Select links for the page and add them to the page, load outgoing links
	 * or all links (outgoing links and incoming links)
	 * @param page the page
	 * @param loadAllLinks indicates if all links are loaded
	 */
	public void loadLinks(Page page, boolean loadAllLinks) {
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			StringBuilder sql = new StringBuilder(SELECT_LINKS);
			if (loadAllLinks) {
				sql.append(SELECT_LINKS_INCOMING);
			}

			preStatement = connection.prepareStatement(sql.toString());

			JdbcTools.setString(preStatement, 1, page.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, 2, page.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, 3, page.getLink().getLinkPath().getProtocol());
			if (loadAllLinks) {
				JdbcTools.setString(preStatement, 4, page.getLink().getDomain().getName());
				JdbcTools.setString(preStatement, 5, page.getLink().getLinkPath().getPath());
				JdbcTools.setString(preStatement, 6, page.getLink().getLinkPath().getProtocol());
			}

			result = preStatement.executeQuery();
			while (result.next()) {
				setLinks(page, result);
			}

			Collections.sort(page.getIncomingInternLinks());
			Collections.sort(page.getIncomingExternLinks());
			Collections.sort(page.getOutgoingInternLinks());
			Collections.sort(page.getOutgoingExternLinks());

		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to load links", e);
		} finally {
			close(result, preStatement);
		}
	}

	/**
	 * Select interesting found page for crawling
	 * @param forbiddenDomainList the list of domain which must not crawl
	 * @param crawlConfig the crawl config
	 * @return the interesting found page
	 */
	public Page getInterestingFoundPage(List<Domain> forbiddenDomainList, CrawlConfig crawlConfig) {
		Page page = null;
		StringBuilder sql = new StringBuilder(SELECT_INTERESTING_FOUND_PAGE_START);

		if (crawlConfig.getInnerDeep() != -1) {
			sql.append(SELECT_INTERESTING_FOUND_PAGE_INNER_DEEP);
		}

		if (crawlConfig.getOuterDeep() != -1) {
			sql.append(SELECT_INTERESTING_FOUND_PAGE_OUTER_DEEP);
		}

		if (!crawlConfig.getForceCrawl()) {
			sql.append(SELECT_INTERESTING_FOUND_PAGE_ALLOW_CRAWL);
		}

		if (!forbiddenDomainList.isEmpty()) {
			sql.append(SELECT_INTERESTING_FOUND_PAGE_FORBIDDEN_DOMAIN);
			int forbiddenDomainSize = forbiddenDomainList.size();
			for (int i = 1; i < forbiddenDomainSize; i++) {
				sql.append(", ?");
			}
			sql.append(" ) ");
		}

		sql.append(SELECT_INTERESTING_FOUND_PAGE_END);

		PreparedStatement preStatement = null;
		ResultSet result = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			JdbcTools.setInteger(preStatement, i++, crawlConfig.getMinimumScore());
			if (crawlConfig.getInnerDeep() != -1) {
				JdbcTools.setInteger(preStatement, i++, crawlConfig.getInnerDeep());
			}
			if (crawlConfig.getOuterDeep() != -1) {
				JdbcTools.setInteger(preStatement, i++, crawlConfig.getOuterDeep());
			}
			for (Domain forbiddenDomain : forbiddenDomainList) {
				JdbcTools.setString(preStatement, i++, forbiddenDomain.getName());
			}

			preStatement.setMaxRows(1);
			result = preStatement.executeQuery();
			if (result.next()) {
				page = toPageWithoutContent(result);
			}

		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get interesting found page", e);
		} finally {
			close(result, preStatement);
		}

		return page;
	}

	/**
	 * Select the page and return it with all information
	 * @param link the link of the page
	 * @return the page
	 */
	public Page getPageWithAllInformation(Link link) {
		Page page = null;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(SELECT_PAGE);
			JdbcTools.setString(preStatement, 1, link.getDomain().getName());
			JdbcTools.setString(preStatement, 2, link.getLinkPath().getPath());
			JdbcTools.setString(preStatement, 3, link.getLinkPath().getProtocol());
			result = preStatement.executeQuery();
			if (result.next()) {
				page = toPageWithContent(result);
				loadLinks(page, true);
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get page with all information", e);
		} finally {
			close(result, preStatement);
		}

		return page;
	}

	/**
	 * Select pages which are starting points
	 * @return the list of pages
	 */
	public List<Page> getStartingPageList() {
		List<Page> startingPageList = new ArrayList<Page>();
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(SELECT_STARTING_PAGE_LIST);
			result = preStatement.executeQuery();
			while (result.next()) {
				Page page = toPageWithoutContent(result);
				startingPageList.add(page);
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get starting page list", e);
		} finally {
			close(result, preStatement);
		}

		return startingPageList;
	}

	/**
	 * Return sql params for filtering, build from overviewParams
	 * @param params the overviewParams
	 * @return the stringBuilder with sql params
	 */
	private StringBuilder getSqlFilterToDisplay(OverviewParams params) {
		StringBuilder filter = new StringBuilder();

		if (params.isSelectCrawled()) {
			filter.append(FILTER_TO_DISPLAY_CRAWLED);
		}

		if (params.isSelectJustFound()) {
			if (filter.length() > 0) {
				filter.append(OR);
			}
			filter.append(FILTER_TO_DISPLAY_JUST_FOUND);
		}

		if (params.isSelectError()) {
			if (filter.length() > 0) {
				filter.append(OR);
			}
			filter.append(FILTER_TO_DISPLAY_ERROR);
		}

		if (params.isSelectRedirectPage()) {
			if (filter.length() > 0) {
				filter.append(OR);
			}
			filter.append(FILTER_TO_DISPLAY_REDIRECT_PAGE);
		}

		return filter;
	}

	/**
	 * Return sql params for orderging, build from overviewParams
	 * @param params the overviewParams
	 * @return the stringBuilder with sql params
	 */
	private StringBuilder getSqlOrderToDisplay(OverviewParams params) {
		StringBuilder orderby = new StringBuilder();

		switch (params.getOrder()) {
		case SCORE:
			orderby.append(ORDER_TO_DISPLAY_SCORE);
			break;
		case DEEP:
			orderby.append(ORDER_TO_DISPLAY_DEEP);
			break;
		case CRAWLTIME:
			orderby.append(ORDER_TO_DISPLAY_CRAWL_TIME);
			break;
		case NAME:
		default:
			orderby.append(ORDER_TO_DISPLAY_DEFAULT);
			break;
		}

		return orderby;
	}

	/**
	 * Select list of paths from a domain, build from overviewParams
	 * @param domain the domain
	 * @param params the overviewParams
	 * @return list of link paths
	 */
	public List<LinkPath> getPathListToDisplay(Domain domain, OverviewParams params) {
		List<LinkPath> pathList = new ArrayList<LinkPath>();

		StringBuilder filter = getSqlFilterToDisplay(params);
		StringBuilder orderby = getSqlOrderToDisplay(params);

		StringBuilder sql = new StringBuilder(SELECT_PATH_LIST_TO_DISPLAY_START);
		if (filter.length() > 0) {
			sql.append(" AND ( ").append(filter).append(" ) ");
		}
		sql.append(orderby);

		PreparedStatement preStatement = null;
		ResultSet result = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			JdbcTools.setString(preStatement, 1, domain.getName());
			result = preStatement.executeQuery();
			while (result.next()) {
				String protocol = JdbcTools.getString(result, PROTOCOL);
				String path = JdbcTools.getString(result, PATH);
				pathList.add(new LinkPath(protocol, path));
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get paths to display", e);
		} finally {
			close(result, preStatement);
		}

		return pathList;
	}

	/**
	 * Return the number of paths, build from overviewParams
	 * @param params the overviewParams
	 * @return the number of paths
	 */
	public Integer getPathNumberToDisplay(OverviewParams params) {
		Integer pathNumber = null;
		StringBuilder filter = getSqlFilterToDisplay(params);

		StringBuilder sql = new StringBuilder(SELECT_PATH_NUMBER_TO_DISPLAY_START);
		if (filter.length() > 0) {
			sql.append(" AND ( ").append(filter).append(" ) ");
		}

		PreparedStatement preStatement = null;
		ResultSet result = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			result = preStatement.executeQuery();
			if (result.next()) {
				pathNumber = JdbcTools.getInteger(result, "countPath");
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get path number to display", e);
		} finally {
			close(result, preStatement);
		}

		return pathNumber;
	}

	/**
	 * 
	 * @return
	 */
	public PageDbIterator getCrawledPagesWithContent() {
		PageDbIterator pageIterator = new PageDbIterator();

		try {
			PreparedStatement preStatement = connection.prepareStatement(SELECT_CRAWLED_PAGES_WITH_CONTENT);
			ResultSet result = preStatement.executeQuery();

			while (result.next()) {
				pageIterator = new PageDbIterator(result, true);
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get pages for recalculating", e);
		}

		return pageIterator;
	}

	/**
	 * Return the number of crawled pages
	 * @return the number of crawled pages
	 */
	public int getCrawledPagesNumber() {
		Integer crawledPagesNumber = 0;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(COUNT_CRAWLED_PAGES_NUMBER);
			result = preStatement.executeQuery();
			result.next();
			crawledPagesNumber = JdbcTools.getInteger(result, "crawledPagesNumber");
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to get crawled pages number", e);
		} finally {
			close(result, preStatement);
		}

		return crawledPagesNumber;
	}

	// UPDATE AND INSERT

	/**
	 * Update the crawled page
	 * @param crawledPage the crawled page
	 */
	public void updateCrawledPage(Page crawledPage) {
		int internLinksNumber = crawledPage.getOutgoingInternLinks().size();
		int externLinksNumber = crawledPage.getOutgoingExternLinks().size();

		// Update page
		StringBuilder sql = new StringBuilder(UPDATE_CRAWLED_PAGE_START);
		if (crawledPage.getCrawlError() != null) {
			sql.append(UPDATE_CRAWLED_PAGE_CRAWL_ERROR);
		}
		if (crawledPage.getContent() != null) {
			sql.append(UPDATE_CRAWLED_PAGE_CRAWL_CONTENT);
		}
		sql.append(UPDATE_CRAWLED_PAGE_END);

		PreparedStatement preStatement = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			int i = 1;

			JdbcTools.setDouble(preStatement, i++, crawledPage.getScore());
			if (crawledPage.getCrawlError() != null) {
				JdbcTools.setString(preStatement, i++, crawledPage.getCrawlError());
			}
			if (crawledPage.getContent() != null) {
				JdbcTools.setClob(preStatement, i++, crawledPage.getContent());
			}

			JdbcTools.setDate(preStatement, i++, crawledPage.getCrawlTime());
			JdbcTools.setBoolean(preStatement, i++, crawledPage.getRedirectPage());
			JdbcTools.setBoolean(preStatement, i++, false);
			JdbcTools.setInteger(preStatement, i++, internLinksNumber);
			JdbcTools.setInteger(preStatement, i++, externLinksNumber);
			JdbcTools.setString(preStatement, i++, crawledPage.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, i++, crawledPage.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, i++, crawledPage.getLink().getLinkPath().getProtocol());

			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update crawled page", e);
		} finally {
			close(preStatement);
		}

		// Insert links (but before delete old links)
		try {
			preStatement = connection.prepareStatement(DELETE_LINKS);
			JdbcTools.setString(preStatement, 1, crawledPage.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, 2, crawledPage.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, 3, crawledPage.getLink().getLinkPath().getProtocol());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to delete links", e);
		} finally {
			close(preStatement);
		}

		if (internLinksNumber + externLinksNumber > 0) {
			sql = new StringBuilder(INSERT_LINKS);
			for (int i = 1; i < internLinksNumber + externLinksNumber; i++) {
				sql.append(INSERT_LINKS_PARAMS);
			}

			try {
				preStatement = connection.prepareStatement(sql.toString());
				List<Link> outgoingLinks = new ArrayList<Link>(crawledPage.getOutgoingInternLinks());
				outgoingLinks.addAll(crawledPage.getOutgoingExternLinks());
				int paramIndex = 1;
				for (Link link : outgoingLinks) {
					JdbcTools.setString(preStatement, paramIndex++, crawledPage.getLink().getDomain().getName());
					JdbcTools.setString(preStatement, paramIndex++, crawledPage.getLink().getLinkPath().getPath());
					JdbcTools.setString(preStatement, paramIndex++, crawledPage.getLink().getLinkPath().getProtocol());
					JdbcTools.setString(preStatement, paramIndex++, link.getDomain().getName());
					JdbcTools.setString(preStatement, paramIndex++, link.getLinkPath().getPath());
					JdbcTools.setString(preStatement, paramIndex++, link.getLinkPath().getProtocol());
				}
				preStatement.executeUpdate();
			} catch (SQLException e) {
				Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to insert links", e);
			} finally {
				close(preStatement);
			}
		}
	}

	/**
	 * Update the found page (found after crawling)
	 * @param foundPage the found page
	 */
	public void updateFoundPage(Page foundPage) {
		Page foundPageInDatabase = null;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(SELECT_PAGE);
			JdbcTools.setString(preStatement, 1, foundPage.getLink().getDomain().getName());
			JdbcTools.setString(preStatement, 2, foundPage.getLink().getLinkPath().getPath());
			JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getProtocol());
			result = preStatement.executeQuery();
			if (result.next()) {
				foundPageInDatabase = toPageWithoutContent(result);
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to select found page", e);
		} finally {
			close(result, preStatement);
		}

		// if foundPage doesn't exist in database
		if (foundPageInDatabase == null) {
			try {
				preStatement = connection.prepareStatement(INSERT_PAGE);
				JdbcTools.setString(preStatement, 1, foundPage.getLink().getDomain().getName());
				JdbcTools.setString(preStatement, 2, foundPage.getLink().getLinkPath().getPath());
				JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getProtocol());
				JdbcTools.setInteger(preStatement, 4, foundPage.getInnerDeep());
				JdbcTools.setInteger(preStatement, 5, foundPage.getOuterDeep());
				JdbcTools.setDouble(preStatement, 6, foundPage.getScore());
				JdbcTools.setBoolean(preStatement, 7, foundPage.getCrawlNow());
				preStatement.executeUpdate();
			} catch (SQLException e) {
				if (!e.getSQLState().equals(DUPLICATE_KEY_SQL_STATE)) {
					Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to insert found page", e);
				}
			} finally {
				close(preStatement);
			}

		} else {
			// if foundPage exists in database
			try {

				if (foundPageInDatabase.getOuterDeep().intValue() > foundPage.getOuterDeep().intValue()) {
					preStatement = connection.prepareStatement(UPDATE_OUTER_DEEP);
					JdbcTools.setInteger(preStatement, 1, foundPage.getOuterDeep());
					JdbcTools.setString(preStatement, 2, foundPage.getLink().getDomain().getName());
					JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getPath());
					JdbcTools.setString(preStatement, 4, foundPage.getLink().getLinkPath().getProtocol());
					JdbcTools.setInteger(preStatement, 5, foundPage.getOuterDeep());
					preStatement.executeUpdate();

				} else if (foundPageInDatabase.getOuterDeep().intValue() == foundPage.getOuterDeep().intValue()
						&& foundPageInDatabase.getInnerDeep().intValue() > foundPage.getInnerDeep().intValue()) {
					preStatement = connection.prepareStatement(UPDATE_INNER_DEEP);
					JdbcTools.setInteger(preStatement, 1, foundPage.getInnerDeep());
					JdbcTools.setString(preStatement, 2, foundPage.getLink().getDomain().getName());
					JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getPath());
					JdbcTools.setString(preStatement, 4, foundPage.getLink().getLinkPath().getProtocol());
					JdbcTools.setInteger(preStatement, 5, foundPage.getInnerDeep());
					JdbcTools.setInteger(preStatement, 6, foundPage.getOuterDeep());
					preStatement.executeUpdate();
				}

				close(preStatement);

				if (foundPage.getCrawlNow()) {
					preStatement = connection.prepareStatement(UPDATE_CRAWL_NOW);
					JdbcTools.setBoolean(preStatement, 1, foundPage.getCrawlNow());
					JdbcTools.setString(preStatement, 2, foundPage.getLink().getDomain().getName());
					JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getPath());
					JdbcTools.setString(preStatement, 4, foundPage.getLink().getLinkPath().getProtocol());
					preStatement.executeUpdate();

					// "else if" used because we dont update the score if
					// CRAWL_NOW = true
				} else if (foundPageInDatabase.getCrawlTime() == null
						&& foundPageInDatabase.getScore() < foundPage.getScore()) {
					preStatement = connection.prepareStatement(UPDATE_FOUND_PAGE_SCORE);
					JdbcTools.setDouble(preStatement, 1, foundPage.getScore());
					JdbcTools.setString(preStatement, 2, foundPage.getLink().getDomain().getName());
					JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getPath());
					JdbcTools.setString(preStatement, 4, foundPage.getLink().getLinkPath().getProtocol());
					JdbcTools.setDouble(preStatement, 5, foundPage.getScore());
					preStatement.executeUpdate();
				}
			} catch (SQLException e) {
				Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update found page", e);
			} finally {
				close(preStatement);
			}
		}

		try {
			if (!foundPage.getIncomingInternLinks().isEmpty()) {
				preStatement = connection.prepareStatement(UPDATE_INCOMING_INTERN_LINKS_NUMBER);
				JdbcTools.setString(preStatement, 1, foundPage.getLink().getDomain().getName());
				JdbcTools.setString(preStatement, 2, foundPage.getLink().getLinkPath().getPath());
				JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getProtocol());
				preStatement.executeUpdate();
			} else if (!foundPage.getIncomingExternLinks().isEmpty()) {
				preStatement = connection.prepareStatement(UPDATE_INCOMING_EXTERN_LINKS_NUMBER);
				JdbcTools.setString(preStatement, 1, foundPage.getLink().getDomain().getName());
				JdbcTools.setString(preStatement, 2, foundPage.getLink().getLinkPath().getPath());
				JdbcTools.setString(preStatement, 3, foundPage.getLink().getLinkPath().getProtocol());
				preStatement.executeUpdate();
			}
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update found page", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Update the column "allowCrawl" for a page ("allowCrawl" indicates if the
	 * robots.txt allow to crawl)
	 * @param pageLink the link of a page
	 * @param allowCrawl the value of "allowCrawl"
	 */
	public void updateAllowCrawlPage(Link pageLink, boolean allowCrawl) {
		PreparedStatement preStatement = null;

		try {
			preStatement = connection.prepareStatement(UPDATE_ALLOW_CRAWL);
			JdbcTools.setBoolean(preStatement, 1, allowCrawl);
			JdbcTools.setString(preStatement, 2, pageLink.getDomain().getName());
			JdbcTools.setString(preStatement, 3, pageLink.getLinkPath().getPath());
			JdbcTools.setString(preStatement, 4, pageLink.getLinkPath().getProtocol());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update allow crawl page", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Update the column "crawlNow" for a page ("crawlNow" permits to prioritize
	 * the page for crawling)
	 * @param pageLink the link of a page
	 * @param crawlNow the value of "crawlNow"
	 */
	public void updateCrawlNow(Link pageLink, boolean crawlNow) {
		PreparedStatement preStatement = null;
		try {
			preStatement = connection.prepareStatement(UPDATE_CRAWL_NOW);
			JdbcTools.setBoolean(preStatement, 1, crawlNow);
			JdbcTools.setString(preStatement, 2, pageLink.getDomain().getName());
			JdbcTools.setString(preStatement, 3, pageLink.getLinkPath().getPath());
			JdbcTools.setString(preStatement, 4, pageLink.getLinkPath().getProtocol());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update crawl now page", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Set all scores to null (init for recalculating)
	 */
	public void initAllScores() {
		PreparedStatement preStatement = null;
		try {
			preStatement = connection.prepareStatement(UPDATE_INIT_ALL_SCORES);
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to init all scores", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Update the score of a page
	 * @param pageLink the link of a page
	 * @param score the score
	 * @param crawledPage indicates if the page is already crawled
	 */
	public void updateScorePage(Link pageLink, Double score) {
		StringBuilder sql = new StringBuilder(UPDATE_SCORE_PAGE);

		PreparedStatement preStatement = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			JdbcTools.setDouble(preStatement, 1, score);
			JdbcTools.setString(preStatement, 2, pageLink.getDomain().getName());
			JdbcTools.setString(preStatement, 3, pageLink.getLinkPath().getPath());
			JdbcTools.setString(preStatement, 4, pageLink.getLinkPath().getProtocol());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE, "Error to update score page", e);
		} finally {
			close(preStatement);
		}
	}

}
