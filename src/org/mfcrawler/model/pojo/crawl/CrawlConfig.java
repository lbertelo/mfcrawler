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

package org.mfcrawler.model.pojo.crawl;

/**
 * Describes the configuration of a crawl project
 * 
 * @author lbertelo
 */
public class CrawlConfig implements ICrawlConfigParams {

	/**
	 * Number of crawling thread
	 */
	private Integer threadNumber;

	/**
	 * Maximum of inner deep for crawling ("< 0" means no maximum) inner deep is
	 * the distance between pages in a same site
	 */
	private Integer innerDeep;

	/**
	 * Maximum of outer deep for crawling ("< 0" means no maximum) outer deep is
	 * the distance between sites
	 */
	private Integer outerDeep;

	/**
	 * The number of remaining crawled page before stopping crawl
	 */
	private Integer remainingCrawlNumber;

	/**
	 * The minimum score for page considered interesting
	 */
	private Integer minimumScore;

	/**
	 * The delay (in milliseconds) between two crawls on a same site if the
	 * Crawl-delay of robots.txt doesn't exist
	 */
	private Integer crawlDelay;

	/**
	 * Indicates if indications of the robots.txt must be forced
	 */
	private Boolean forceCrawl;

	/**
	 * The delay (in milliseconds) between two crawls if the indications of
	 * robots.txt are forced
	 */
	private Integer forceCrawlDelay;

	/**
	 * Default constructor which initialize attributes with their default values
	 */
	public CrawlConfig() {
		threadNumber = THREAD_NUMBER_DEFAULT;
		innerDeep = INNER_DEEP_DEFAULT;
		outerDeep = OUTER_DEEP_DEFAULT;
		remainingCrawlNumber = REMAINING_CRAWL_NUMBER_DEFAULT;
		minimumScore = MINIMUM_SCORE_DEFAULT;
		crawlDelay = CRAWL_DELAY_DEFAULT;
		forceCrawl = FORCE_CRAWL_DEFAULT;
		forceCrawlDelay = FORCE_CRAWL_DELAY_DEFAULT;
	}

	/**
	 * Getter of thread number
	 * @return the thread number
	 */
	public Integer getThreadNumber() {
		return threadNumber;
	}

	/**
	 * Setter of thread number
	 * @param threadNumber the thread number
	 */
	public void setThreadNumber(Integer threadNumber) {
		this.threadNumber = threadNumber;
	}

	/**
	 * Getter of inner deep
	 * @return the inner deep
	 */
	public Integer getInnerDeep() {
		return innerDeep;
	}

	/**
	 * Setter of inner deep
	 * @param innerDeep the inner deep
	 */
	public void setInnerDeep(Integer innerDeep) {
		this.innerDeep = innerDeep;
	}

	/**
	 * Getter of outer deep
	 * @return the outer deep
	 */
	public Integer getOuterDeep() {
		return outerDeep;
	}

	/**
	 * Setter of outer deep
	 * @param outerDeep the outer deep
	 */
	public void setOuterDeep(Integer outerDeep) {
		this.outerDeep = outerDeep;
	}

	/**
	 * Getter of remaining crawl number
	 * @return the remaining crawl number
	 */
	public Integer getRemainingCrawlNumber() {
		return remainingCrawlNumber;
	}

	/**
	 * Setter of remaining crawl number
	 * @param remainingCrawlNumber the remaining crawl number
	 */
	public void setRemainingCrawlNumber(Integer remainingCrawlNumber) {
		this.remainingCrawlNumber = remainingCrawlNumber;
	}

	/**
	 * Getter of minimum score
	 * @return the minimum score
	 */
	public Integer getMinimumScore() {
		return minimumScore;
	}

	/**
	 * Setter of minimum score
	 * @param minimumScore the minimum score
	 */
	public void setMinimumScore(Integer minimumScore) {
		this.minimumScore = minimumScore;
	}

	/**
	 * Getter of crawl delay
	 * @return the crawl delay
	 */
	public Integer getCrawlDelay() {
		return crawlDelay;
	}

	/**
	 * Setter of crawl delay
	 * @param crawlDelay the crawl delay
	 */
	public void setCrawlDelay(Integer crawlDelay) {
		this.crawlDelay = crawlDelay;
	}

	/**
	 * Getter of force crawl
	 * @return the boolean force crawl
	 */
	public Boolean getForceCrawl() {
		return forceCrawl;
	}

	/**
	 * Setter of force crawl
	 * @param forceCrawl the boolean force crawl
	 */
	public void setForceCrawl(Boolean forceCrawl) {
		this.forceCrawl = forceCrawl;
	}

	/**
	 * Getter of force crawl delay
	 * @return the force crawl delay
	 */
	public Integer getForceCrawlDelay() {
		return forceCrawlDelay;
	}

	/**
	 * Setter of force crawl delay
	 * @param forceCrawlDelay the force crawl delay
	 */
	public void setForceCrawlDelay(Integer forceCrawlDelay) {
		this.forceCrawlDelay = forceCrawlDelay;
	}

}
