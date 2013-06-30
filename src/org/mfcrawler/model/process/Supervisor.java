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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.SwingPropertyChangeModel;
import org.mfcrawler.model.pojo.crawl.CrawlOrder;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.crawl.CrawlResponse;
import org.mfcrawler.model.process.crawl.CrawlThread;
import org.mfcrawler.model.process.crawl.CrawlThreadInfo;
import org.mfcrawler.model.util.I18nUtil;

/**
 * The Supervisor manages the crawl thread and mediates the model
 * 
 * @author lbertelo
 */
public class Supervisor implements ICrawlManager {

	/**
	 * The swing property change model
	 */
	private SwingPropertyChangeModel propertyChangeModel;

	/**
	 * The crawl project
	 */
	private CrawlProject crawlProject;

	/**
	 * List of the crawl threads
	 */
	private List<CrawlThread> crawlThreadList;

	/**
	 * The foundPageManager
	 */
	private FoundPageManager foundPageManager;

	/**
	 * Indicates if the crawls are started
	 */
	private boolean start;

	/**
	 * Number of remaining crawl
	 */
	private Integer remainingCrawlNumber;

	/**
	 * Default constructor
	 * @param propertyChangeModel
	 * @param crawlProject
	 */
	public Supervisor(SwingPropertyChangeModel propertyChangeModel, CrawlProject crawlProject) {
		this.propertyChangeModel = propertyChangeModel;
		this.crawlProject = crawlProject;
		crawlThreadList = new ArrayList<CrawlThread>();
		foundPageManager = new FoundPageManager(crawlProject);

		start = false;
		remainingCrawlNumber = crawlProject.getCrawlConfig().getRemainingCrawlNumber();
	}

	// Model method

	/**
	 * Setter of remainingCrawlNumber
	 * @param number the number of remaining crawl
	 */
	public void setRemainingCrawlNumber(Integer number) {
		remainingCrawlNumber = number;
	}

	/**
	 * Indicates if the crawls are started
	 * @return true if the crawls are started, false otherwise
	 */
	public boolean isStarted() {
		return start;
	}

	/**
	 * Indicates if the crawls are stopped
	 * @return true if all the crawls are stopped, false otherwise
	 */
	public boolean isStopped() {
		return (!start && crawlThreadList.isEmpty());
	}

	/**
	 * Starts the supervisor (launch the crawl threads and initialize the
	 * foundPageManager)
	 */
	public void startCrawl() {
		if (testBeforeStart()) {
			start = true;
			foundPageManager.init();

			launchCrawlThread();
			propertyChangeModel.notify(IPropertyName.PROCESSING, I18nUtil.getMessage("processing.inProgress"));
		} else {
			propertyChangeModel.notify(IPropertyName.PROCESSING, I18nUtil.getMessage("processing.error"));
		}
	}

	/**
	 * Stops the crawl threads
	 */
	public void stopCrawl() {
		if (start) {
			propertyChangeModel.notify(IPropertyName.PROCESSING, I18nUtil.getMessage("processing.stopping"));
		}
		start = false;
	}

	/**
	 * Launches the necessary crawl threads
	 */
	public synchronized void launchCrawlThread() {
		if (crawlThreadList.size() < crawlProject.getCrawlConfig().getThreadNumber()) {
			for (int i = crawlThreadList.size(); i < crawlProject.getCrawlConfig().getThreadNumber(); i++) {
				CrawlThread crawlThread = new CrawlThread(this, i);
				crawlThread.start();
				crawlThreadList.add(crawlThread);
			}
			propertyChangeModel.notify(IPropertyName.LAUNCHED_THREADS, crawlProject.getCrawlConfig().getThreadNumber());
		}
	}

	/**
	 * Launches tests before start the supervisor
	 * @return true if all the tests succeed, false otherwise
	 */
	private boolean testBeforeStart() {
		// Test de Vérification Réseau
		boolean netConnection = true;
		try {
			URL serverAddr = new URL("http://www.google.com");
			HttpURLConnection conn = (HttpURLConnection) serverAddr.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(2000);
			conn.connect();
			if (conn.getResponseCode() >= 400) {
				netConnection = false;
			}
		} catch (Exception e) {
			netConnection = false;
		}
		if (!netConnection) {
			propertyChangeModel.notify(IPropertyName.ERROR, "Error with your internet access");
			return false;
		}
		return true;
	}

	// CrawlThread method

	@Override
	public synchronized CrawlThreadInfo initCrawlThread(Integer crawlThreadId) {
		CrawlThreadInfo crawlThreadInfo = new CrawlThreadInfo(crawlThreadId, propertyChangeModel);
		crawlThreadInfo.noticeStart();
		return crawlThreadInfo;
	}

	@Override
	public synchronized CrawlOrder manageCrawlThread(Integer crawlThreadId, CrawlResponse response) {
		if (response != null) {
			foundPageManager.addCrawlDate(response.getLink().getDomain(), response.getCrawlDate());
		}

		if (crawlThreadId == 0) {
			propertyChangeModel.notify(IPropertyName.SITES_PAGES_NUMBER, null);
		}

		if (remainingCrawlNumber == 0) {
			stopCrawl();
		}

		CrawlOrder order;
		if (crawlThreadId >= crawlProject.getCrawlConfig().getThreadNumber() || !start) {
			CrawlThread.wakeUpAll();
			order = new CrawlOrder();
			if (crawlThreadId + 1 == crawlThreadList.size()) {
				order.setStop(true);
			} else {
				order.setWait(2000);
			}
		} else {
			CrawlThread.wakeUpOne();
			order = foundPageManager.getFoundPage();

			if (order.getLink() != null) {
				Logger.getLogger(Supervisor.class.getName()).log(Level.INFO, "Crawl : " + order.getLink());

				if (remainingCrawlNumber > 0 && !order.getLink().isRobotsTxt()) {
					remainingCrawlNumber--;
					propertyChangeModel.notify(IPropertyName.REMAINING_CRAWL_NUMBER, remainingCrawlNumber);
				}
			}
		}

		return order;
	}

	@Override
	public void finalizeCrawlThread(Integer crawlThreadId) {
		CrawlThread crawlThread = crawlThreadList.get(crawlThreadId);
		crawlThread.getCrawlInfo().noticeStop();
		crawlThreadList.remove(crawlThread);

		propertyChangeModel.notify(IPropertyName.LAUNCHED_THREADS, Integer.valueOf(crawlThreadList.size()));
		if (crawlThreadList.isEmpty()) {
			propertyChangeModel.notify(IPropertyName.PROCESSING, I18nUtil.getMessage("processing.stopped"));
			propertyChangeModel.notify(IPropertyName.SITES_PAGES_NUMBER, null);
		}

		CrawlThread.wakeUpAll();
	}

}
