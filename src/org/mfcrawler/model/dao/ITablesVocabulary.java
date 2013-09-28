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

package org.mfcrawler.model.dao;

/**
 * Constants for DAO classes Contain names of tables and columns
 * 
 * @author lbertelo
 */
public interface ITablesVocabulary {

	String TABLE_PAGE = "PAGE";
	String TABLE_PAGE_P = TABLE_PAGE + ".";
	String DOMAIN = "DOMAIN";
	String PATH = "PATH";
	String PROTOCOL = "PROTOCOL";
	String CONTENT = "CONTENT";
	String SCORE = "SCORE";
	String INNER_DEEP = "INNER_DEEP";
	String OUTER_DEEP = "OUTER_DEEP";
	String CRAWL_TIME = "CRAWL_TIME";
	String ALLOW_CRAWL = "ALLOW_CRAWL";
	String REDIRECT_PAGE = "REDIRECT_PAGE";
	String CRAWL_NOW = "CRAWL_NOW";
	String CRAWL_ERROR = "CRAWL_ERROR";
	String INCOMING_INTERN_LINKS_NUMBER = "INCOMING_INTERN_LINKS_NUMBER";
	String INCOMING_EXTERN_LINKS_NUMBER = "INCOMING_EXTERN_LINKS_NUMBER";
	String OUTGOING_INTERN_LINKS_NUMBER = "OUTGOING_INTERN_LINKS_NUMBER";
	String OUTGOING_EXTERN_LINKS_NUMBER = "OUTGOING_EXTERN_LINKS_NUMBER";

	String TABLE_LINK = "LINK";
	String TABLE_LINK_P = TABLE_LINK + ".";
	String LINK_DOMAIN = "LINK_DOMAIN";
	String LINK_PATH = "LINK_PATH";
	String LINK_PROTOCOL = "LINK_PROTOCOL";

	String TABLE_SITE = "SITE";
	String TABLE_SITE_P = TABLE_SITE + ".";
	String ROOT_DOMAIN = "ROOT_DOMAIN";
	String BLACKLISTED = "BLACKLISTED";
	String ROBOT_FILE_EXIST = "ROBOT_FILE_EXIST";
	String ROBOT_FILE_CONTENT = "ROBOT_FILE_CONTENT";

	String DUPLICATE_KEY_SQL_STATE = "23505";

	String TEST_QUERY = "SELECT " + DOMAIN + ", " + PATH + " FROM " + TABLE_PAGE + " ORDER BY " + DOMAIN;

	String OR = " OR ";

}
