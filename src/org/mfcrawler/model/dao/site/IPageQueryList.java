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

import org.mfcrawler.model.dao.ITablesVocabulary;

/**
 * SQL queries for PageDao
 * 
 * @author lbertelo
 */
public interface IPageQueryList extends ITablesVocabulary {

	// Create and drop

	String CREATE_TABLES = " CREATE TABLE " + TABLE_PAGE + " ( " + DOMAIN + " VARCHAR(255) NOT NULL, " + PATH
			+ " VARCHAR(2048) NOT NULL, " + PROTOCOL + " VARCHAR(10) NOT NULL, " + CONTENT + " CLOB, " + SCORE
			+ " DOUBLE, " + INNER_DEEP + " INTEGER NOT NULL, " + OUTER_DEEP + " INTEGER NOT NULL, " + CRAWL_TIME
			+ " TIMESTAMP, " + ALLOW_CRAWL + " BOOLEAN, " + REDIRECT_PAGE + " BOOLEAN, " + CRAWL_NOW + " BOOLEAN, "
			+ CRAWL_ERROR + " VARCHAR(20000), " + INCOMING_INTERN_LINKS_NUMBER + " INTEGER DEFAULT 0, "
			+ INCOMING_EXTERN_LINKS_NUMBER + " INTEGER DEFAULT 0, " + OUTGOING_INTERN_LINKS_NUMBER
			+ " INTEGER DEFAULT 0, " + OUTGOING_EXTERN_LINKS_NUMBER + " INTEGER DEFAULT 0, " + "PRIMARY KEY ( "
			+ DOMAIN + ", " + PATH + ", " + PROTOCOL + " ) ); "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + DOMAIN + "_INDEX " + "ON " + TABLE_PAGE + " ( " + DOMAIN + " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + PATH + "_INDEX " + "ON " + TABLE_PAGE + " ( " + PATH + " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + PROTOCOL + "_INDEX " + "ON " + TABLE_PAGE + " ( " + PROTOCOL
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + SCORE + "_INDEX " + "ON " + TABLE_PAGE + " ( " + SCORE + " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + CRAWL_TIME + "_INDEX " + "ON " + TABLE_PAGE + " ( " + CRAWL_TIME
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + INNER_DEEP + "_INDEX " + "ON " + TABLE_PAGE + " ( " + INNER_DEEP
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + OUTER_DEEP + "_INDEX " + "ON " + TABLE_PAGE + " ( " + OUTER_DEEP
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + ALLOW_CRAWL + "_INDEX " + "ON " + TABLE_PAGE + " ( " + ALLOW_CRAWL
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + CRAWL_NOW + "_INDEX " + "ON " + TABLE_PAGE + " ( " + CRAWL_NOW
			+ " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + INCOMING_INTERN_LINKS_NUMBER + "_INDEX " + "ON " + TABLE_PAGE
			+ " ( " + INCOMING_INTERN_LINKS_NUMBER + " );  "

			+ " CREATE INDEX " + TABLE_PAGE + "_" + INCOMING_EXTERN_LINKS_NUMBER + "_INDEX " + "ON " + TABLE_PAGE
			+ " ( " + INCOMING_EXTERN_LINKS_NUMBER + " );  "

			+ " CREATE TABLE " + TABLE_LINK + " ( " + DOMAIN + " VARCHAR(255) NOT NULL, " + PATH
			+ " VARCHAR(2048) NOT NULL, " + PROTOCOL + " VARCHAR(10) NOT NULL, " + LINK_DOMAIN
			+ " VARCHAR(255) NOT NULL, " + LINK_PATH + " VARCHAR(2048) NOT NULL, " + LINK_PROTOCOL
			+ " VARCHAR(10) NOT NULL, PRIMARY KEY (" + DOMAIN + ", " + PATH + ", " + PROTOCOL + ", " + LINK_DOMAIN
			+ ", " + LINK_PATH + ", " + LINK_PROTOCOL + " ) );  "

			+ " CREATE INDEX " + TABLE_LINK + "_" + DOMAIN + "_INDEX " + "ON " + TABLE_LINK + " ( " + DOMAIN + " );  "

			+ " CREATE INDEX " + TABLE_LINK + "_" + LINK_DOMAIN + "_INDEX " + "ON " + TABLE_LINK + " ( " + LINK_DOMAIN
			+ " );  ";

	String DROP_TABLES = " DROP TABLE " + TABLE_PAGE + " ; " + " DROP TABLE " + TABLE_LINK + " ; ";

	// Select

	String SELECT_ALL_PAGE = " SELECT * FROM " + TABLE_PAGE;

	String SELECT_PAGE = " SELECT * FROM " + TABLE_PAGE + " WHERE " + DOMAIN + " = ? AND " + PATH + " =  ? AND "
			+ PROTOCOL + " = ? ";

	String SELECT_LINKS = " SELECT " + TABLE_LINK_P + "*, " + TABLE_PAGE_P + CRAWL_TIME + " FROM " + TABLE_LINK
			+ " LEFT JOIN " + TABLE_PAGE + " ON (" + TABLE_PAGE_P + DOMAIN + " = " + TABLE_LINK_P + LINK_DOMAIN
			+ " AND " + TABLE_PAGE_P + PATH + " = " + TABLE_LINK_P + LINK_PATH + " ) WHERE ( " + TABLE_LINK_P + DOMAIN
			+ " = ? " + " AND " + TABLE_LINK_P + PATH + " = ? AND " + TABLE_LINK_P + PROTOCOL + " = ? ) ";

	String SELECT_LINKS_INCOMING = " OR ( " + TABLE_LINK_P + LINK_DOMAIN + " = ? " + " AND " + TABLE_LINK_P + LINK_PATH
			+ " = ? AND " + TABLE_LINK_P + LINK_PROTOCOL + " = ? ) ";

	String SELECT_INTERESTING_FOUND_PAGE_START = " SELECT * FROM " + TABLE_PAGE + " WHERE ( ( " + CRAWL_TIME
			+ " IS NULL  AND " + SCORE + " >= ? ) OR " + CRAWL_NOW + " = true ) " + " AND " + DOMAIN + " NOT IN "
			+ " ( SELECT " + DOMAIN + " FROM " + TABLE_SITE + " WHERE " + BLACKLISTED + " = true ) ";

	String SELECT_INTERESTING_FOUND_PAGE_INNER_DEEP = " AND " + INNER_DEEP + " <= ? ";

	String SELECT_INTERESTING_FOUND_PAGE_OUTER_DEEP = " AND " + OUTER_DEEP + " <= ? ";

	String SELECT_INTERESTING_FOUND_PAGE_ALLOW_CRAWL = " AND (" + ALLOW_CRAWL + " = true OR " + ALLOW_CRAWL
			+ " IS NULL ) ";

	String SELECT_INTERESTING_FOUND_PAGE_FORBIDDEN_DOMAIN = " AND " + DOMAIN + " NOT IN ( ?";

	String SELECT_INTERESTING_FOUND_PAGE_END = " ORDER BY " + CRAWL_NOW + " DESC, " + SCORE + " DESC, "
			+ INCOMING_EXTERN_LINKS_NUMBER + " DESC, " + INCOMING_INTERN_LINKS_NUMBER + " DESC ";

	String SELECT_STARTING_PAGE_LIST = " SELECT * FROM " + TABLE_PAGE + " WHERE " + INNER_DEEP + " = 0 " + " AND "
			+ OUTER_DEEP + " = 0 " + " ORDER BY " + DOMAIN + " ASC, " + PATH + " ASC ";

	String SELECT_PATH_LIST_TO_DISPLAY_START = " SELECT " + PATH + ", " + PROTOCOL + " FROM " + TABLE_PAGE + " WHERE "
			+ DOMAIN + " = ? ";

	String FILTER_TO_DISPLAY_CRAWLED = " ( " + CRAWL_TIME + " IS NOT NULL  AND " + REDIRECT_PAGE + " = false AND "
			+ CRAWL_ERROR + " IS NULL ) ";

	String FILTER_TO_DISPLAY_JUST_FOUND = " " + CRAWL_TIME + " IS NULL ";

	String FILTER_TO_DISPLAY_ERROR = " ( " + CRAWL_TIME + " IS NOT NULL AND " + CRAWL_ERROR + " IS NOT NULL ) ";

	String FILTER_TO_DISPLAY_REDIRECT_PAGE = " ( " + CRAWL_TIME + " IS NOT NULL  AND " + REDIRECT_PAGE + " = true AND "
			+ CRAWL_ERROR + " IS NULL ) ";

	String ORDER_TO_DISPLAY_SCORE = " ORDER BY " + SCORE + " DESC ";

	String ORDER_TO_DISPLAY_DEEP = " ORDER BY " + OUTER_DEEP + " ASC, " + INNER_DEEP + " ASC ";

	String ORDER_TO_DISPLAY_CRAWL_TIME = " ORDER BY " + CRAWL_TIME + " ASC ";

	String ORDER_TO_DISPLAY_DEFAULT = " ORDER BY " + PATH + " ASC ";

	String SELECT_PATH_NUMBER_TO_DISPLAY_START = " SELECT COUNT(" + PATH + ") as countPath FROM " + TABLE_PAGE
			+ " WHERE " + DOMAIN + " NOT IN ( SELECT " + DOMAIN + " FROM " + TABLE_SITE + " WHERE " + BLACKLISTED
			+ " = true ) ";

	String SELECT_CRAWLED_PAGES_FOR_RECALCULATING = " SELECT * FROM " + TABLE_PAGE + " WHERE " + CRAWL_TIME
			+ " IS NOT NULL AND " + CONTENT + " IS NOT NULL ";

	String COUNT_CRAWLED_PAGES_NUMBER = " SELECT COUNT(*) as crawledPagesNumber FROM " + TABLE_PAGE + " WHERE "
			+ CRAWL_TIME + " IS NOT NULL " + " AND " + DOMAIN + " NOT IN " + " ( SELECT " + DOMAIN + " FROM "
			+ TABLE_SITE + " WHERE " + BLACKLISTED + " = true ) ";

	// Update and delete

	String UPDATE_CRAWLED_PAGE_START = " UPDATE " + TABLE_PAGE + " SET " + SCORE + " = ?, ";

	String UPDATE_CRAWLED_PAGE_CRAWL_ERROR = CRAWL_ERROR + " = ?, ";

	String UPDATE_CRAWLED_PAGE_CRAWL_CONTENT = CONTENT + " = ?, ";

	String UPDATE_CRAWLED_PAGE_END = CRAWL_TIME + " = ?, " + REDIRECT_PAGE + " = ?, " + CRAWL_NOW + " = ?, "
			+ OUTGOING_INTERN_LINKS_NUMBER + " = ?, " + OUTGOING_EXTERN_LINKS_NUMBER + " = ?  WHERE " + DOMAIN
			+ " = ? AND " + PATH + " = ? AND " + PROTOCOL + " = ?";

	String DELETE_LINKS = " DELETE FROM " + TABLE_LINK + " WHERE " + DOMAIN + " = ? AND " + PATH + " =  ? AND "
			+ PROTOCOL + " = ? ";

	String INSERT_LINKS = " INSERT INTO " + TABLE_LINK + " ( " + DOMAIN + ", " + PATH + ", " + PROTOCOL + ", "
			+ LINK_DOMAIN + ", " + LINK_PATH + ", " + LINK_PROTOCOL + " ) VALUES ( ?, ?, ?, ?, ?, ? )";

	String INSERT_LINKS_PARAMS = ", ( ?, ?, ?, ?, ?, ? )";

	String INSERT_PAGE = " INSERT INTO " + TABLE_PAGE + " ( " + DOMAIN + ", " + PATH + ", " + PROTOCOL + ", "
			+ INNER_DEEP + ", " + OUTER_DEEP + ", " + SCORE + ", " + CRAWL_NOW + " ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";

	String UPDATE_OUTER_DEEP = " UPDATE " + TABLE_PAGE + " SET " + OUTER_DEEP + " = ?  WHERE " + DOMAIN + " = ? AND "
			+ PATH + " = ? AND " + PROTOCOL + " = ? AND " + OUTER_DEEP + " > ? ";

	String UPDATE_INNER_DEEP = " UPDATE " + TABLE_PAGE + " SET " + INNER_DEEP + " = ?  WHERE " + DOMAIN + " = ? AND "
			+ PATH + " = ? AND " + PROTOCOL + " = ? AND " + INNER_DEEP + " > ? AND " + OUTER_DEEP + " = ? ";

	String UPDATE_FOUND_PAGE_SCORE = " UPDATE " + TABLE_PAGE + " SET " + SCORE + " = ?  WHERE " + DOMAIN + " = ? AND "
			+ PATH + " = ? AND " + PROTOCOL + " = ? AND " + CRAWL_TIME + " IS NULL " + " AND " + SCORE + " < ? ";

	String UPDATE_CRAWL_NOW = " UPDATE " + TABLE_PAGE + " SET " + CRAWL_NOW + " = ?  WHERE " + DOMAIN + " = ? AND "
			+ PATH + " = ? AND " + PROTOCOL + " = ? ";

	String UPDATE_INCOMING_INTERN_LINKS_NUMBER = " UPDATE " + TABLE_PAGE + " SET " + INCOMING_INTERN_LINKS_NUMBER
			+ " = " + INCOMING_INTERN_LINKS_NUMBER + " + 1 " + " WHERE " + DOMAIN + " = ? AND " + PATH + " = ? AND "
			+ PROTOCOL + " = ? ";

	String UPDATE_INCOMING_EXTERN_LINKS_NUMBER = " UPDATE " + TABLE_PAGE + " SET " + INCOMING_EXTERN_LINKS_NUMBER
			+ " = " + INCOMING_EXTERN_LINKS_NUMBER + " + 1 " + " WHERE " + DOMAIN + " = ? AND " + PATH + " = ? AND "
			+ PROTOCOL + " = ? ";

	String UPDATE_ALLOW_CRAWL = " UPDATE " + TABLE_PAGE + " SET " + ALLOW_CRAWL + " = ?  WHERE " + DOMAIN + " = ? AND "
			+ PATH + " = ? AND " + PROTOCOL + " = ? ";

	String UPDATE_INIT_ALL_SCORES = " UPDATE " + TABLE_PAGE + " SET " + SCORE + " = NULL ";

	String UPDATE_SCORE_PAGE = " UPDATE " + TABLE_PAGE + " SET " + SCORE + " = ?  WHERE " + DOMAIN + " = ? AND " + PATH
			+ " = ? AND " + PROTOCOL + " = ? ";

}
