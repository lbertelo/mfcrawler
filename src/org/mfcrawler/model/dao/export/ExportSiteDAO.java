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
import org.mfcrawler.model.dao.site.ISiteQueryList;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;

/**
 * DAO for Sites, specific to export
 * 
 * @author lbertelo
 */
public class ExportSiteDAO extends BaseDAO implements ISiteQueryList {

	/**
	 * Query which selects sites allowed to export
	 */
	private static final String EXPORT_SITE_LIST = " SELECT " + TABLE_SITE_P + DOMAIN + " as " + DOMAIN + " , SUM( "
			+ TABLE_PAGE_P + SCORE + " ) as totalScore " + " , COUNT( " + TABLE_PAGE_P + PATH
			+ " ) as crawledPagesNumber " + " FROM " + TABLE_SITE + " LEFT JOIN " + TABLE_PAGE + " ON " + TABLE_PAGE_P
			+ DOMAIN + " = " + TABLE_SITE_P + DOMAIN + " WHERE ( " + TABLE_SITE_P + BLACKLISTED + " = false OR "
			+ TABLE_SITE_P + BLACKLISTED + " IS NULL ) AND " + TABLE_PAGE_P + CRAWL_TIME + " IS NOT NULL "
			+ " GROUP BY " + TABLE_SITE_P + DOMAIN + " HAVING SUM( " + TABLE_PAGE_P + SCORE + " ) >= ? " + " ORDER BY "
			+ TABLE_SITE_P + DOMAIN + " ASC ";

	/**
	 * Query which selects target domains to export
	 */
	private static final String EXPORT_TARGET_DOMAINS_START = " SELECT " + LINK_DOMAIN + " FROM " + TABLE_LINK
			+ " WHERE " + DOMAIN + " = ? AND " + DOMAIN + " <> " + LINK_DOMAIN + " AND " + LINK_DOMAIN + " IN ( ";

	/**
	 * "Order by" for query which selects target domains to export
	 */
	private static final String EXPORT_TARGET_DOMAINS_ORDER = " ORDER BY " + LINK_DOMAIN + " ASC ";

	/**
	 * Select all sites allowed to export with a minimum total score
	 * @param minTotalScore the minimum total score accepted
	 * @return the list of sites
	 */
	public List<Site> getSiteListToExport(Double minTotalScore) {
		//TODO Exporter les sites et les pages non-crawlés ?
		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Site> siteListToExport = new ArrayList<Site>();

		try {
			preStatement = connection.prepareStatement(EXPORT_SITE_LIST);
			JdbcTools.setDouble(preStatement, 1, minTotalScore);
			result = preStatement.executeQuery();

			while (result.next()) {
				String domain = JdbcTools.getString(result, DOMAIN);
				Double totalScore = JdbcTools.getDouble(result, "totalScore");
				Integer crawledPagesNumber = JdbcTools.getInteger(result, "crawledPagesNumber");

				PreparedStatement preStatement2 = connection.prepareStatement(SELECT_SITE);
				JdbcTools.setString(preStatement2, 1, domain);
				ResultSet result2 = preStatement2.executeQuery();
				if (result2.next()) {
					Site site = SiteDAO.toSite(result2);
					site.setTotalScore(totalScore);
					site.setCrawledPagesNumber(crawledPagesNumber);
					close(result2, preStatement2);
					siteListToExport.add(site);
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(ExportSiteDAO.class.getName()).log(Level.SEVERE, "Error to get site list to export", e);
		} finally {
			close(result, preStatement);
		}

		return siteListToExport;
	}

	/**
	 * Select all domains of sites allowed to export with a minimum total score
	 * @param minTotalScore the minimum total score accepted
	 * @return the list of domains
	 */
	public List<Domain> getDomainListToExport(Double minTotalScore) {
		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Domain> domainListToExport = new ArrayList<Domain>();

		try {
			preStatement = connection.prepareStatement(EXPORT_SITE_LIST);
			JdbcTools.setDouble(preStatement, 1, minTotalScore);
			result = preStatement.executeQuery();

			while (result.next()) {
				String domainStr = JdbcTools.getString(result, DOMAIN);
				domainListToExport.add(new Domain(domainStr));
			}
		} catch (SQLException e) {
			Logger.getLogger(ExportSiteDAO.class.getName()).log(Level.SEVERE, "Error to get domain list to export", e);
		} finally {
			close(result, preStatement);
		}

		return domainListToExport;
	}

	/**
	 * Select target links from a source domain and a list of domains
	 * @param sourceDomain the source domain
	 * @param domainList domains which are selected to export
	 * @return the list of target domains
	 */
	public List<Domain> getTargetDomainList(Domain sourceDomain, List<Domain> domainList) {
		PreparedStatement preStatement = null;
		ResultSet result = null;
		List<Domain> targetDomainList = new ArrayList<Domain>();

		StringBuilder sql = new StringBuilder(EXPORT_TARGET_DOMAINS_START);
		int numberOfDomains = domainList.size();
		if (numberOfDomains > 0) {
			sql.append("?");
			for (int i = 1; i < numberOfDomains; i++) {
				sql.append(", ?");
			}
		}
		sql.append(" ) ");
		sql.append(EXPORT_TARGET_DOMAINS_ORDER);

		try {
			preStatement = connection.prepareStatement(sql.toString());
			int i = 1;
			JdbcTools.setString(preStatement, i++, sourceDomain.getName());
			for (Domain domain : domainList) {
				JdbcTools.setString(preStatement, i++, domain.getName());
			}

			result = preStatement.executeQuery();
			while (result.next()) {
				Domain domain = new Domain(JdbcTools.getString(result, LINK_DOMAIN));
				targetDomainList.add(domain);
			}
		} catch (SQLException e) {
			Logger.getLogger(ExportSiteDAO.class.getName()).log(Level.SEVERE, "Error to get target domain list", e);
		} finally {
			close(result, preStatement);
		}

		return targetDomainList;
	}
}
