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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.htmlparser.jericho.Source;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.dao.DbmsManager;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.ApplicationConfig;
import org.mfcrawler.model.pojo.crawl.CrawlOrder;
import org.mfcrawler.model.pojo.crawl.CrawlResponse;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.process.ICrawlManager;
import org.mfcrawler.model.process.KeywordManager;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.model.util.extraction.PageExtractionUtil;

/**
 * CrawlThread executes the crawling action
 * 
 * @author lbertelo
 */
public class CrawlThread extends Thread {

	/**
	 * CrawlThreadInfo for searching a page to crawl
	 */
	private static final String CI_SEARCH = I18nUtil.getMessage("crawlThread.search");

	/**
	 * Object used to wait the thread
	 */
	private static final Object WAIT_OBJECT = new Object();

	/**
	 * The crawlManager
	 */
	private ICrawlManager crawlManager;

	/**
	 * Identifier of the crawl thread
	 */
	private Integer id;

	/**
	 * The crawl thread informant
	 */
	private CrawlThreadInfo crawlInfo;

	/**
	 * The keyword manager
	 */
	private KeywordManager keywordManager;

	/**
	 * User agent used by the crawler
	 */
	private String userAgent;

	/**
	 * Page request timeout used by the crawler
	 */
	private Integer pageRequestTimeout;

	/**
	 * Robots request timeout used by the crawler
	 */
	private Integer robotsRequestTimeout;

	/**
	 * Table of forbidden file extensions
	 */
	private String forbiddenFileExtensions[];

	/**
	 * Connection to the database
	 */
	private Connection connectionDb;

	/**
	 * Page DAO
	 */
	private PageDAO pageDao;

	/**
	 * Site DAO
	 */
	private SiteDAO siteDao;

	/**
	 * Default constructor
	 * @param crawlManager the crawlManager
	 * @param id the crawl identifier
	 */
	public CrawlThread(ICrawlManager crawlManager, Integer id) {
		super("Crawl " + id);
		this.crawlManager = crawlManager;
		this.id = id;
		crawlInfo = crawlManager.initCrawlThread(id);
		keywordManager = KeywordManager.get();

		ApplicationConfig config = ApplicationModel.getConfig();
		userAgent = config.getUserAgent();
		pageRequestTimeout = config.getPageRequestTimeout();
		robotsRequestTimeout = config.getRobotsRequestTimeout();
		forbiddenFileExtensions = config.getForbiddenFileExtensions().split("\\|");

		connectionDb = DbmsManager.get().getNewConnection();
		pageDao = new PageDAO(connectionDb);
		siteDao = new SiteDAO(connectionDb);
	}

	/**
	 * Getter of crawl thread informant
	 * @return the crawl thread info
	 */
	public CrawlThreadInfo getCrawlInfo() {
		return crawlInfo;
	}

	/**
	 * Wakes up all the crawl threads
	 */
	public static void wakeUpAll() {
		synchronized (WAIT_OBJECT) {
			WAIT_OBJECT.notifyAll();
		}
	}

	/**
	 * Wakes up one crawl thread
	 */
	public static void wakeUpOne() {
		synchronized (WAIT_OBJECT) {
			WAIT_OBJECT.notify();
		}
	}

	/**
	 * Puts the thread on waiting
	 * @param timeout the time to wait in milliseconds.
	 */
	public static void waitThread(long timeout) {
		synchronized (WAIT_OBJECT) {
			try {
				WAIT_OBJECT.wait(timeout);
			} catch (InterruptedException e) {
				Logger.getLogger(CrawlThread.class.getName()).log(Level.WARNING, "Error to wait thread", e);
			}
		}
	}

	// MAIN

	@Override
	public void run() {
		// Check before launching the loop
		if (connectionDb != null) {
			loopCrawl();
		}

		// Close the thread database connection
		try {
			if (connectionDb != null) {
				connectionDb.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(CrawlThread.class.getName()).log(Level.WARNING, "Error to close DB connection", e);
		}
		// Check before launching the loop

		crawlManager.finalizeCrawlThread(id);
	}

	/**
	 * Loops while the crawl manager don't say to stop
	 */
	private void loopCrawl() {
		boolean stop = false;
		CrawlOrder order;
		CrawlResponse response = null;

		while (!stop) {
			order = crawlManager.manageCrawlThread(id, response);
			stop = order.getStop();
			if (!stop) {
				if (order.getWait() != 0) {
					// no response because no crawl
					response = null;
					crawlInfo.notice(CrawlThreadInfo.CI_WAIT);
					waitThread(order.getWait().longValue());
				} else {
					response = new CrawlResponse(order);
					response.setCrawlDate(new Date());

					// perform a crawl for a site or for a page
					if (order.getLink().isRobotsTxt()) {
						performCrawlSite(order);
					} else {
						performCrawlPage(order);
					}

					crawlInfo.notice(CI_SEARCH);
				}
			}
		}
	}

	/**
	 * Perfoms a crawl for a page
	 * @param order the crawl order
	 */
	private void performCrawlPage(CrawlOrder order) {
		Page page = new Page(order.getLink());
		page.setInnerDeep(order.getInnerDeep());
		page.setOuterDeep(order.getOuterDeep());
		page.setScore(order.getExpectedScore());
		page.setCrawlTime(new Date());

		HttpURLConnection httpConnection = null;
		try {
			// Opening connection
			crawlInfo.notice(CrawlThreadInfo.CI_LOAD + order.getLink());
			httpConnection = getHttpConnection(order.getLink());
			httpConnection.setReadTimeout(pageRequestTimeout);

			// Sends the request
			httpConnection.connect();
			if (httpConnection.getResponseCode() >= 400) {
				page.setCrawlError("HTTP RESPONSE CODE : " + httpConnection.getResponseCode());
			} else if (httpConnection.getContentType() != null && !httpConnection.getContentType().startsWith("text")) {
				page.setCrawlError("CONTENT TYPE : " + httpConnection.getContentType());
			}

		} catch (Exception e) {
			Logger.getLogger(CrawlThread.class.getName()).log(Level.INFO, "Crawl error", e);
			page.setCrawlError(e.toString());
		}

		// Read content
		if (page.getCrawlError() == null) {
			crawlInfo.notice(CrawlThreadInfo.CI_READ + order.getLink());

			try {
				// Parses the html content using Jericho
				Source parsedContent = new Source(httpConnection);
				parsedContent.setLogger(null);
				parsedContent.fullSequentialParse();
				page.setContent(parsedContent.getTextExtractor().toString().trim());

				// Redirect request
				if (httpConnection.getResponseCode() >= 300 || page.getContent().isEmpty()) {
					page.setRedirectPage(true);
					PageExtractionUtil.redirectLinksExtraction(page, parsedContent, forbiddenFileExtensions);
				} else {
					page.setRedirectPage(false);
					PageExtractionUtil.pageLinksExtraction(page, parsedContent, forbiddenFileExtensions);
				}

			} catch (Exception e) {
				Logger.getLogger(CrawlThread.class.getName()).log(Level.INFO, "Crawl error", e);
				page.setCrawlError(e.toString());
			}

			if (page.getCrawlError() == null && !page.getRedirectPage()) {
				// Calculates score
				crawlInfo.notice(CrawlThreadInfo.CI_CALC + order.getLink());
				Double score = keywordManager.calculate(page.getContent());
				page.setScore(score);
			}

		}

		if (page.getCrawlError() != null) {
			page.setScore(0.0);
		}

		// Sends information to Database
		crawlInfo.notice(CrawlThreadInfo.CI_SAVE + order.getLink());
		savePageInDataBase(page);
	}

	/**
	 * Perfoms a crawl for a robots.txt
	 * @param order the crawl order
	 */
	private void performCrawlSite(CrawlOrder order) {
		Site site = new Site(order.getLink().getDomain());
		site.setRobotFileExist(true);
		site.setCrawlTime(new Date());

		String robotFileContent = null;

		HttpURLConnection httpConnection = null;
		try {
			// Opening connection
			crawlInfo.notice(CrawlThreadInfo.CI_LOAD + order.getLink());
			httpConnection = getHttpConnection(order.getLink());
			httpConnection.setReadTimeout(robotsRequestTimeout);

			// Sends the request
			httpConnection.connect();
			if (httpConnection.getResponseCode() >= 400
					|| (httpConnection.getContentType() != null && !httpConnection.getContentType().startsWith("text"))) {
				site.setRobotFileExist(false);
			}

		} catch (Exception e) {
			Logger.getLogger(CrawlThread.class.getName()).log(Level.INFO, "Crawl error", e);
			site.setRobotFileExist(false);
		}

		// Read content
		if (site.getRobotFileExist()) {
			crawlInfo.notice(CrawlThreadInfo.CI_READ + order.getLink());
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				String line = null;
				StringBuilder contentBuffer = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					contentBuffer.append(line).append("\n");
				}
				robotFileContent = contentBuffer.toString();
			} catch (Exception e) {
				site.setRobotFileExist(false);
			}
		}

		// Sends information to Database
		crawlInfo.notice(CrawlThreadInfo.CI_SAVE + order.getLink());
		siteDao.updateSiteRobot(site, robotFileContent);
	}

	// CONNECTION

	/**
	 * Get the http connection (works with "http" and "https")
	 * @param link the link
	 * @return the http connection
	 * @throws IOException
	 */
	private HttpURLConnection getHttpConnection(Link link) throws IOException {
		URL serverAddr = new URL(link.getUrl());
		HttpURLConnection httpConnection = (HttpURLConnection) serverAddr.openConnection();

		httpConnection.setRequestMethod("GET");
		httpConnection.setRequestProperty("User-Agent", userAgent);

		return httpConnection;
	}

	// SAVE

	/**
	 * Saves page information in database
	 * @param page the page
	 */
	private void savePageInDataBase(Page page) {
		// TODO ajouter des commits et des rollbacks ?
		pageDao.updateCrawledPage(page);

		List<Link> outgoingLink = new ArrayList<Link>();
		outgoingLink.addAll(page.getOutgoingExternLinks());
		outgoingLink.addAll(page.getOutgoingInternLinks());
		for (Link link : outgoingLink) {
			Page foundPage = new Page(link);
			foundPage.setCrawlNow(false);
			foundPage.setScore(page.getScore());

			if (page.getLink().getDomain().equals(link.getDomain())) {
				foundPage.setInnerDeep(page.getInnerDeep() + 1);
				foundPage.setOuterDeep(page.getOuterDeep());
				foundPage.getIncomingInternLinks().add(page.getLink());
			} else {
				foundPage.setInnerDeep(0);
				foundPage.setOuterDeep(page.getOuterDeep() + 1);
				foundPage.getIncomingExternLinks().add(page.getLink());
			}

			pageDao.updateFoundPage(foundPage);
			siteDao.insertEmptySite(link.getDomain());
		}
	}

}
