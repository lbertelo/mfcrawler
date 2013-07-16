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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.dao.BaseDAO;
import org.mfcrawler.model.dao.JdbcTools;
import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.util.extraction.RobotExtractionUtil;

/**
 * DAO for Sites
 * 
 * @author lbertelo
 */
public class SiteDAO extends BaseDAO implements ISiteQueryList {

	/**
	 * Default constructor which uses the main database connection
	 */
	public SiteDAO() {
		super();
	}

	/**
	 * Constructor which uses the connection passed in parameter
	 * @param connection connection used
	 */
	public SiteDAO(Connection connection) {
		super(connection);
	}

	// TABLES CREATION

	/**
	 * Return the sql queries which permits to create tables referring to sites
	 * @return the sql queries
	 */
	public static String getSqlTablesCreation() {
		return CREATE_TABLES;
	}

	/**
	 * Return the sql queries which permits to delete tables referring to sites
	 * @return the sql queries
	 */
	public static String getSqlTablesDeletion() {
		return DROP_TABLES;
	}

	// BASE

	/**
	 * Extract a site from a resultSet
	 * @param result the resultSet
	 * @return the site
	 */
	public static Site toSite(ResultSet result) {
		Site site = null;
		try {
			site = new Site(new Domain(JdbcTools.getString(result, DOMAIN)));
			site.setBlacklisted(JdbcTools.getBoolean(result, BLACKLISTED));
			site.setCrawlTime(JdbcTools.getDate(result, CRAWL_TIME));
			site.setRobotFileExist(JdbcTools.getBoolean(result, ROBOT_FILE_EXIST));
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to read site in DB", e);
		}

		return site;
	}

	/**
	 * Extract a link from a resultSet and add it to the site
	 * @param site the site
	 * @param result the resultSet
	 */
	private static void setDomainLinks(Site site, ResultSet result) {
		try {
			Domain base = new Domain(JdbcTools.getString(result, DOMAIN));
			Domain link = new Domain(JdbcTools.getString(result, LINK_DOMAIN));

			if (site.getDomain().equals(base)) {
				if (!site.getOutgoingDomains().contains(link)) {
					site.getOutgoingDomains().add(link);
				}
			} else {
				if (!site.getIncomingDomains().contains(base)) {
					site.getIncomingDomains().add(base);
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to set domain links", e);
		}
	}

	// SELECT

	/**
	 * Get site with robots.txt information from domain
	 * @param domain the domain
	 * @return the site
	 */
	public Site getSiteWithRobotInfo(Domain domain) {
		Site site = null;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(SELECT_SITE);
			JdbcTools.setString(preStatement, 1, domain.getName());
			result = preStatement.executeQuery();
			if (result.next()) {
				site = toSite(result);
				String robotFileContent = JdbcTools.getClob(result, ROBOT_FILE_CONTENT);
				RobotExtractionUtil.extraction(site, robotFileContent);
			}
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to get site with robot info", e);
		} finally {
			close(result, preStatement);
		}

		return site;
	}

	/**
	 * Get site with all information from domain
	 * @param domain the domain
	 * @return the site
	 */
	public Site getSiteWithAllInformation(Domain domain) {
		Site site = null;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			// site data
			preStatement = connection.prepareStatement(SELECT_SITE);
			JdbcTools.setString(preStatement, 1, domain.getName());
			result = preStatement.executeQuery();
			if (result.next()) {
				site = toSite(result);
				String robotFileContent = JdbcTools.getClob(result, ROBOT_FILE_CONTENT);
				RobotExtractionUtil.extraction(site, robotFileContent);

				close(result, preStatement);

				// totalScore and CrawledPagesNumber
				preStatement = connection.prepareStatement(SELECT_SITE_SCORE);
				JdbcTools.setString(preStatement, 1, domain.getName());
				result = preStatement.executeQuery();
				if (result.next()) {
					Double totalScore = JdbcTools.getDouble(result, "totalScore");
					site.setTotalScore(totalScore);
					Integer crawledPagesNumber = JdbcTools.getInteger(result, "crawledPagesNumber");
					site.setCrawledPagesNumber(crawledPagesNumber);
				} else {
					site.setCrawledPagesNumber(0);
				}

				close(result, preStatement);

				// min outer deep
				preStatement = connection.prepareStatement(SELECT_MIN_OUTER_DEEP);
				JdbcTools.setString(preStatement, 1, domain.getName());
				result = preStatement.executeQuery();
				if (result.next()) {
					Integer minOuterDeep = JdbcTools.getInteger(result, "minOuterDeep");
					site.setMinOuterDeep(minOuterDeep);
				}

				close(result, preStatement);

				// domain links
				preStatement = connection.prepareStatement(SELECT_LINKS);
				JdbcTools.setString(preStatement, 1, domain.getName());
				JdbcTools.setString(preStatement, 2, domain.getName());
				result = preStatement.executeQuery();
				while (result.next()) {
					setDomainLinks(site, result);
				}

				Collections.sort(site.getIncomingDomains());
				Collections.sort(site.getOutgoingDomains());
			}
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to get site with all information", e);
		} finally {
			close(result, preStatement);
		}

		return site;
	}

	/**
	 * Return sql params for filtering, build from overviewParams
	 * @param params the overviewParams
	 * @return the stringBuilder with sql params
	 */
	private StringBuilder getSqlFilterToDiplay(OverviewParams params) {
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
	 * Select list of domains, build from overviewParams
	 * @param params the overviewParams
	 * @return the list of domains
	 */
	public List<Domain> getDomainListToDisplay(OverviewParams params) {
		List<Domain> domainList = new ArrayList<Domain>();

		StringBuilder select = new StringBuilder(), orderby = new StringBuilder();
		switch (params.getOrder()) {
		case SCORE:
			select.append(SELECT_TO_DISPLAY_SCORE);
			orderby.append(ORDER_TO_DISPLAY_SCORE);
			break;
		case DEEP:
			select.append(SELECT_TO_DISPLAY_DEEP);
			orderby.append(ORDER_TO_DISPLAY_DEEP);
			break;
		case CRAWLTIME:
			orderby.append(ORDER_TO_DISPLAY_CRAWLTIME);
			break;
		case NAME:
		default:
			orderby.append(ORDER_TO_DISPLAY_NAME);
			break;
		}

		StringBuilder filter = getSqlFilterToDiplay(params);

		StringBuilder sql = new StringBuilder();
		sql.append(SELECT_DOMAIN_LIST_TO_DISPLAY_START1).append(select).append(SELECT_DOMAIN_LIST_TO_DISPLAY_START2);
		if (filter.length() > 0) {
			sql.append(" AND ( ").append(filter).append(" ) ");
		}
		sql.append(SELECT_DOMAIN_LIST_TO_DISPLAY_END);
		sql.append(orderby);

		PreparedStatement preStatement = null;
		ResultSet result = null;
		try {
			preStatement = connection.prepareStatement(sql.toString());
			result = preStatement.executeQuery();
			while (result.next()) {
				Domain domain = new Domain(JdbcTools.getString(result, DOMAIN));
				domainList.add(domain);
			}
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to get domains to display", e);
		} finally {
			close(result, preStatement);
		}

		return domainList;
	}

	/**
	 * Return the number of sites, build from overviewParams
	 * @param params the overviewParams
	 * @return the number of sites
	 */
	public int getCrawledSitesNumber() {
		Integer crawledSitesNumber = 0;
		PreparedStatement preStatement = null;
		ResultSet result = null;

		try {
			preStatement = connection.prepareStatement(COUNT_CRAWLED_SITES_NUMBER);
			result = preStatement.executeQuery();
			if (result.next()) {
				crawledSitesNumber = JdbcTools.getInteger(result, "crawledSitesNumber");
			}
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to get crawled sites number", e);
		} finally {
			close(result, preStatement);
		}

		return crawledSitesNumber;
	}

	// UPDATE AND INSERT

	/**
	 * Update robots.txt information for a site
	 * @param site the site
	 * @param robotFileContent the content of robots.txt
	 */
	public void updateSiteRobot(Site site, String robotFileContent) {
		site.setCrawlTime(new Date());
		PreparedStatement preStatement = null;

		try {
			preStatement = connection.prepareStatement(UPDATE_SITE_ROBOT);
			JdbcTools.setDate(preStatement, 1, site.getCrawlTime());
			JdbcTools.setBoolean(preStatement, 2, site.getRobotFileExist());
			JdbcTools.setClob(preStatement, 3, robotFileContent);
			JdbcTools.setString(preStatement, 4, site.getDomain().getName());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to update robot site", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Set all "blacklisted" to false (init for reblacklisting)
	 */
	public void initBlacklist() {
		PreparedStatement preStatement = null;

		try {
			preStatement = connection.prepareStatement(UPDATE_INIT_BLACKLIST);
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to init blacklist", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Insert a empty site (just domain information)
	 * @param domain the domain of the site
	 */
	public void insertEmptySite(Domain domain) {
		PreparedStatement preStatement = null;

		try {
			preStatement = connection.prepareStatement(INSERT_EMPTY_SITE);
			JdbcTools.setString(preStatement, 1, domain.getName());
			JdbcTools.setString(preStatement, 2, domain.getRootDomain());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			if (!e.getSQLState().equals(DUPLICATE_KEY_SQL_STATE)) {
				Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to insert into blacklist", e);
			}
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Update the column "blacklist" for a site
	 * @param blacklistDomain the domain of the site
	 * @param blacklisted the value of "blacklist"
	 */
	public void updateBlacklist(Domain blacklistDomain, boolean blacklisted) {
		PreparedStatement preStatement = null;

		try {
			String blacklistValue;
			if (blacklistDomain.isFilterBlacklist()) {
				blacklistValue = "%" + blacklistDomain.getName();
			} else {
				blacklistValue = blacklistDomain.getName();
				insertEmptySite(blacklistDomain);
			}

			preStatement = connection.prepareStatement(UPDATE_BLACKLIST);
			JdbcTools.setBoolean(preStatement, 1, blacklisted);
			JdbcTools.setString(preStatement, 2, blacklistValue);
			preStatement.executeUpdate();

		} catch (SQLException e) {
			Logger.getLogger(SiteDAO.class.getName()).log(Level.SEVERE, "Error to update blacklist", e);
		} finally {
			close(preStatement);
		}
	}

	/**
	 * Update the column "crawlNow" for all pages of a site ("crawlNow" permits
	 * to prioritize the page for crawling)
	 * @param domain the domain of the site
	 * @param crawlNow the value of "crawlNow"
	 */
	public void updateCrawlNow(Domain domain, boolean crawlNow) {
		PreparedStatement preStatement = null;

		// This method update all the pages but don't update the site
		try {
			preStatement = connection.prepareStatement(UPDATE_PAGES_CRAWL_NOW);
			JdbcTools.setBoolean(preStatement, 1, crawlNow);
			JdbcTools.setString(preStatement, 2, domain.getName());
			preStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.getLogger(PageDAO.class.getName()).log(Level.SEVERE,
					"Error to update crawl now the pages from the site", e);
		} finally {
			close(preStatement);
		}
	}

}
