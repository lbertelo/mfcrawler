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

package org.mfcrawler.model.process.crawl;

import java.util.HashMap;
import java.util.Map;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.SwingPropertyChangeModel;
import org.mfcrawler.model.util.I18nUtil;

/**
 * Allows to notify the action threads
 * 
 * @author lbertelo
 */
public class CrawlThreadInfo {

	/**
	 * CrawlThreadInfo for waiting
	 */
	public static final String CI_WAIT = I18nUtil.getMessage("crawlThread.wait");

	/**
	 * CrawlThreadInfo for loading
	 */
	public static final String CI_LOAD = I18nUtil.getMessage("crawlThread.load");

	/**
	 * CrawlThreadInfo for reading page
	 */
	public static final String CI_READ = I18nUtil.getMessage("crawlThread.read");

	/**
	 * CrawlThreadInfo for calculating score
	 */
	public static final String CI_CALC = I18nUtil.getMessage("crawlThread.calc");

	/**
	 * CrawlThreadInfo for saving in database
	 */
	public static final String CI_SAVE = I18nUtil.getMessage("crawlThread.save");

	/**
	 * The crawl thread identier
	 */
	private Integer crawlThreadId;

	/**
	 * The swing property change model
	 */
	private SwingPropertyChangeModel propertyChangeModel;

	/**
	 * Default constructor
	 * @param crawlThreadId
	 * @param propertyChangeModel
	 */
	public CrawlThreadInfo(Integer crawlThreadId, SwingPropertyChangeModel propertyChangeModel) {
		this.crawlThreadId = crawlThreadId;
		this.propertyChangeModel = propertyChangeModel;
	}

	/**
	 * Notice a message
	 * @param info the message
	 */
	public void notice(String info) {
		String noticeInfo = "Thread " + (crawlThreadId + 1) + " : " + info;
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(crawlThreadId, noticeInfo);
		sendNotice(map);
	}

	/**
	 * Notice a starting
	 */
	public void noticeStart() {
		notice("Started");
	}

	/**
	 * Notice a stopping
	 */
	public void noticeStop() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(crawlThreadId, null);
		sendNotice(map);
	}

	/**
	 * Send crawl thread notification
	 * @param map the message
	 */
	private void sendNotice(final Map<Integer, String> map) {
		propertyChangeModel.notify(IPropertyName.CRAWL_THREAD_INFO, map);
	}
}
