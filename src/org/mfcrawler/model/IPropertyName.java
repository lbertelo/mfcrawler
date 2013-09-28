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

package org.mfcrawler.model;

/**
 * Constants for notifications
 * 
 * @author lbertelo
 */
public interface IPropertyName {

	// Notify
	String ERROR = "error";
	String WARNING = "warning";
	String INFO = "info";

	// General
	String LOADING = "loading";
	String LOADED = "loaded";
	String PROJECT_LOADED = "projectLoaded";

	// Monitoring Panel
	String PROCESSING = "processing";
	String LAUNCHED_THREADS = "launchedThreads";
	String CRAWL_THREAD_INFO = "crawlThreadInfo";
	String SITES_PAGES_NUMBER = "sitesPagesNumber";
	String REMAINING_CRAWL_NUMBER = "remainingCrawlNumber";

	// Filters Panel
	String ADD_BLACKLIST_DOMAIN = "addBlacklistDomain";

	// Analyze Panel
	String CONTENTS_ANALYZED = "contentsAnalyzed";

}
