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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.crawl.CrawlOrder;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.RobotPath;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Manages the found page to crawl
 * 
 * @author lbertelo
 */
public class FoundPageManager {

	/**
	 * The crawl project
	 */
	private CrawlProject crawlProject;

	/**
	 * Map containing sites waiting
	 */
	private Map<Domain, Site> siteWaitMap;

	/**
	 * Domains currently crawled
	 */
	private List<Domain> domainInProgress;

	/**
	 * Date to wait crawl thread
	 */
	private Date nextWaitDate;

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
	 * @param crawlProject the crawl project
	 */
	public FoundPageManager(CrawlProject crawlProject) {
		this.crawlProject = crawlProject;
		siteWaitMap = new HashMap<Domain, Site>();
		domainInProgress = new ArrayList<Domain>();
	}

	/**
	 * Initialize the foundPageManager
	 */
	public void init() {
		pageDao = new PageDAO();
		siteDao = new SiteDAO();
	}

	/**
	 * Add crawl date to the siteWaitDateMap (Methods launched after a crawl)
	 * @param domain the domain
	 * @param crawlDate the crawlDate
	 */
	public void addCrawlDate(Domain domain, Date crawlDate) {
		Site site = siteDao.getSiteWithRobotInfo(domain);
		site.setCrawlTime(crawlDate);
		siteWaitMap.put(site.getDomain(), site);
		domainInProgress.remove(domain);
	}

	/**
	 * Search a page to crawl
	 * @return the crawl order
	 */
	public CrawlOrder getFoundPage() {
		Page page;
		Boolean findPage = false;
		List<Domain> forbiddenDomain = getForbiddenDomainList();

		do {
			// Get the most interesting found Page
			page = pageDao.getInterestingFoundPage(forbiddenDomain, crawlProject.getCrawlConfig());

			// If no page are found
			if (page == null) {
				CrawlOrder order = new CrawlOrder();
				Integer wait = crawlProject.getCrawlConfig().getCrawlDelay();

				// Try to get a better "wait"
				if (nextWaitDate != null) {
					long difWaitDate = nextWaitDate.getTime() - new Date().getTime();
					if (difWaitDate > 0) {
						wait = new Integer((int) difWaitDate);
					}
				}

				order.setWait(wait);
				return order;
			}

			findPage = checkPage(page, forbiddenDomain);
		} while (!findPage);

		domainInProgress.add(page.getLink().getDomain());
		return new CrawlOrder(page);
	}

	/**
	 * Check if the page can be crawled
	 * @param page the page
	 * @param forbiddenDomain the list of forbidden domains
	 * @return true if the page can be crawled, false otherwise
	 */
	private boolean checkPage(Page page, List<Domain> forbiddenDomain) {
		Domain domain = page.getLink().getDomain();
		Site site = siteDao.getSiteWithRobotInfo(domain);

		// Check if page's domain is blacklisted
		if (site == null || site.getBlacklisted() == null) {
			Iterator<Domain> blacklistIterator = crawlProject.getBlacklistDomains().iterator();
			boolean blacklisted = false;
			while (!blacklisted && blacklistIterator.hasNext()) {
				blacklisted = domain.checkBlacklist(blacklistIterator.next());
			}

			siteDao.updateBlacklist(domain, blacklisted);
			if (blacklisted) {
				return false;
			}
		}

		// Check if page are allowed to crawl
		if (page.getAllowCrawl() == null) {
			if (site == null || site.getRobotFileExist() == null) {
				Link linkRobots = new Link(page.getLink().getDomain());
				page.setLink(linkRobots);
				return true;
			} else {
				boolean allowPath = true;
				for (RobotPath robotPath : site.getRobotPathList()) {
					if (robotPath.checkPath(page.getLink().getLinkPath().getPath())) {
						allowPath = robotPath.isAllow();
					}
				}

				page.setAllowCrawl(allowPath);
				pageDao.updateAllowCrawlPage(page.getLink(), page.getAllowCrawl());
			}
		}

		if (page.getAllowCrawl()) {
			return true;
		} else {
			// Checking for page which are not allowed to crawl
			Site siteWait = siteWaitMap.get(page.getLink().getDomain());
			if (crawlProject.getCrawlConfig().getForceCrawl() && siteWait == null) {
				return true;
			} else {
				forbiddenDomain.add(page.getLink().getDomain());
			}
		}

		return false;
	}

	/**
	 * Constructs the list of forbidden domains
	 * @return the list of forbidden domains
	 */
	private List<Domain> getForbiddenDomainList() {
		List<Domain> domainList = new ArrayList<Domain>();
		domainList.addAll(domainInProgress);

		Date now = new Date();
		nextWaitDate = null;
		Collection<Site> siteWaitMapValues = new ArrayList<Site>(siteWaitMap.values());
		for (Site siteWait : siteWaitMapValues) {
			int delay;

			if (siteWait.getRobotFileExist() && siteWait.getRobotCrawlDelay() != null) {
				delay = siteWait.getRobotCrawlDelay();
			} else {
				delay = crawlProject.getCrawlConfig().getCrawlDelay();
			}

			int forceCrawlDelay = crawlProject.getCrawlConfig().getForceCrawlDelay();
			long time = siteWait.getCrawlTime().getTime();
			Date waitDate = new Date(time + delay);
			Date deleteDate = new Date(time + forceCrawlDelay);

			if (nextWaitDate == null || nextWaitDate.after(waitDate)) {
				nextWaitDate = waitDate;
			}

			if (waitDate.after(now)) {
				domainList.add(siteWait.getDomain());
			} else if (deleteDate.before(now)) {
				siteWaitMap.remove(siteWait.getDomain());
			}
		}

		return domainList;
	}

}
