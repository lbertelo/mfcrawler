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

package org.mfcrawler.model.process;

import org.mfcrawler.model.pojo.crawl.CrawlOrder;
import org.mfcrawler.model.pojo.crawl.CrawlResponse;
import org.mfcrawler.model.process.crawl.CrawlThreadInfo;

/**
 * Allow to manage the crawl threads
 * 
 * @author lbertelo
 */
public interface ICrawlManager {

	/**
	 * Initialize the crawl thread
	 * @param id identifier of the crawl thread
	 * @return the crawl thread informant related
	 */
	CrawlThreadInfo initCrawlThread(Integer id);

	/**
	 * Manage the crawl thread, handle the crawl response and give a crawl order
	 * @param id identifier of the crawl thread
	 * @param response the crawl response
	 * @return the crawl order
	 */
	CrawlOrder manageCrawlThread(Integer id, CrawlResponse response);

	/**
	 * Finalize the crawl thread
	 * @param id id identifier of the crawl thread
	 */
	void finalizeCrawlThread(Integer id);

}
