/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013-2014  lbertelo

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

import org.mfcrawler.model.dao.ITablesVocabulary;

/**
 * SQL queries for SiteDao
 * 
 * @author lbertelo
 */
public interface ISiteQueryList extends ITablesVocabulary {

	// Create and drop

	String CREATE_TABLES = " CREATE TABLE " + TABLE_SITE + " ( " + DOMAIN + " VARCHAR(255) NOT NULL, " + ROOT_DOMAIN
			+ " VARCHAR(255) NOT NULL, " + BLACKLISTED + " BOOLEAN, " + CRAWL_TIME + " TIMESTAMP, " + ROBOT_FILE_EXIST
			+ " BOOLEAN, " + ROBOT_FILE_CONTENT + " CLOB, " + "PRIMARY KEY ( " + DOMAIN + " ) ); "

			+ " CREATE INDEX " + TABLE_SITE + "_" + ROOT_DOMAIN + "_index " + "ON " + TABLE_SITE + " ( " + ROOT_DOMAIN
			+ " ); "

			+ " CREATE INDEX " + TABLE_SITE + "_" + BLACKLISTED + "_index " + "ON " + TABLE_SITE + " ( " + BLACKLISTED
			+ " ); ";

	String DROP_TABLES = " DROP TABLE " + TABLE_SITE + " ; ";

	// Select

	String SELECT_SITE = " SELECT * FROM " + TABLE_SITE + " WHERE " + DOMAIN + " = ? ";

	String SELECT_SITE_SCORE = " SELECT " + TABLE_SITE_P + DOMAIN + " as " + DOMAIN + " , SUM( " + TABLE_PAGE_P + SCORE
			+ " ) as totalScore " + " , COUNT(*) as crawledPagesNumber " + " FROM " + TABLE_SITE + " LEFT JOIN "
			+ TABLE_PAGE + " ON " + TABLE_PAGE_P + DOMAIN + " = " + TABLE_SITE_P + DOMAIN + " WHERE " + TABLE_SITE_P
			+ DOMAIN + " = ? " + " AND " + TABLE_PAGE_P + CRAWL_TIME + " IS NOT NULL " + " GROUP BY " + TABLE_SITE_P
			+ DOMAIN;

	String SELECT_MIN_OUTER_DEEP = " SELECT MIN(" + OUTER_DEEP + ") as minOuterDeep " + " FROM " + TABLE_PAGE
			+ " WHERE " + DOMAIN + " = ? ";

	String SELECT_LINKS = " SELECT " + DOMAIN + " , " + LINK_DOMAIN + " FROM " + TABLE_LINK + " WHERE ( " + DOMAIN
			+ " = ? " + " OR " + LINK_DOMAIN + " = ? ) " + " AND " + DOMAIN + " <> " + LINK_DOMAIN + " GROUP BY "
			+ DOMAIN + " , " + LINK_DOMAIN;

	String SELECT_DOMAIN_LIST_TO_DISPLAY_START1 = " SELECT " + TABLE_SITE_P + DOMAIN + " as " + DOMAIN;

	String SELECT_DOMAIN_LIST_TO_DISPLAY_START2 = " FROM " + TABLE_SITE + " LEFT JOIN " + TABLE_PAGE + " ON "
			+ TABLE_PAGE_P + DOMAIN + " = " + TABLE_SITE_P + DOMAIN + " WHERE ( " + TABLE_SITE_P + BLACKLISTED
			+ " = false OR " + TABLE_SITE_P + BLACKLISTED + " IS NULL ) ";

	String FILTER_TO_DISPLAY_CRAWLED = " ( " + TABLE_PAGE_P + CRAWL_TIME + " IS NOT NULL  AND " + TABLE_PAGE_P
			+ REDIRECT_PAGE + " = false AND " + TABLE_PAGE_P + CRAWL_ERROR + " IS NULL ) ";

	String FILTER_TO_DISPLAY_JUST_FOUND = TABLE_PAGE_P + CRAWL_TIME + " IS NULL ";

	String FILTER_TO_DISPLAY_ERROR = TABLE_PAGE_P + CRAWL_ERROR + " IS NOT NULL ";

	String FILTER_TO_DISPLAY_REDIRECT_PAGE = " ( " + TABLE_PAGE_P
			+ REDIRECT_PAGE + " = true AND " + TABLE_PAGE_P + CRAWL_ERROR + " IS NULL ) ";

	String SELECT_TO_DISPLAY_AVG_SCORE = " , SUM( " + TABLE_PAGE_P + SCORE + " ) / COUNT( " + TABLE_PAGE_P + PATH
			+ " ) as score ";

	String SELECT_TO_DISPLAY_TOTAL_SCORE = ", SUM( " + TABLE_PAGE_P + SCORE + " ) as score ";

	String ORDER_TO_DISPLAY_SCORE = " ORDER BY score DESC ";

	String SELECT_TO_DISPLAY_DEEP = " , MIN( " + TABLE_PAGE_P + OUTER_DEEP + " ) as outerDeepSite ";

	String ORDER_TO_DISPLAY_DEEP = " ORDER BY outerDeepSite ASC ";

	String ORDER_TO_DISPLAY_CRAWLTIME = " ORDER BY " + TABLE_SITE_P + CRAWL_TIME + " ASC ";

	String ORDER_TO_DISPLAY_NAME = " ORDER BY " + TABLE_SITE_P + ROOT_DOMAIN + " ASC " + ", " + TABLE_SITE_P + DOMAIN
			+ " ASC ";

	String SELECT_DOMAIN_LIST_TO_DISPLAY_END = " GROUP BY " + TABLE_SITE_P + DOMAIN + ", " + TABLE_SITE_P + ROOT_DOMAIN;

	String COUNT_CRAWLED_SITES_NUMBER = " SELECT COUNT( DISTINCT " + TABLE_SITE_P + DOMAIN
			+ " ) as crawledSitesNumber " + " FROM " + TABLE_SITE + " LEFT JOIN " + TABLE_PAGE + " ON " + TABLE_PAGE_P
			+ DOMAIN + " = " + TABLE_SITE_P + DOMAIN + " WHERE ( " + TABLE_SITE_P + BLACKLISTED + " = false OR "
			+ TABLE_SITE_P + BLACKLISTED + " IS NULL ) AND " + TABLE_PAGE_P + CRAWL_TIME + " IS NOT NULL ";

	// Update and delete

	String UPDATE_SITE_ROBOT = " UPDATE " + TABLE_SITE + " SET " + CRAWL_TIME + " =  ?, " + ROBOT_FILE_EXIST
			+ " =  ?, " + ROBOT_FILE_CONTENT + " = ? WHERE " + DOMAIN + " =  ? ";

	String UPDATE_INIT_BLACKLIST = " UPDATE " + TABLE_SITE + " SET " + BLACKLISTED + " = false ";

	String INSERT_EMPTY_SITE = " INSERT INTO " + TABLE_SITE + " ( " + DOMAIN + ", " + ROOT_DOMAIN
			+ ") VALUES ( ?, ? ) ";

	String UPDATE_BLACKLIST = " UPDATE " + TABLE_SITE + " SET " + BLACKLISTED + " = ? WHERE " + DOMAIN + " = ? OR "
			+ DOMAIN + " LIKE ? escape '$' ";

	String UPDATE_PAGES_CRAWL_NOW = " UPDATE " + TABLE_PAGE + " SET " + CRAWL_NOW + " = ?  WHERE " + DOMAIN + " = ? ";

}
