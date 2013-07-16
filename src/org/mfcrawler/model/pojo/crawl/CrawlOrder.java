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

import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Describes an order for the crawl threads
 * 
 * @author lbertelo
 */
public class CrawlOrder {

	/**
	 * Link of the page to crawl
	 */
	private Link link;

	/**
	 * Indicates if the crawler must be stopped
	 */
	private Boolean stop;

	/**
	 * Indicates if the crawler must be waited Waiting time in milliseconds
	 */
	private Integer wait;

	/**
	 * Inner deep of the page to crawl
	 */
	private Integer innerDeep;

	/**
	 * Outer deep of the page to crawl
	 */
	private Integer outerDeep;

	/**
	 * The expectedScore of the page (best score of the parent pages)
	 */
	private Double expectedScore;

	/**
	 * Default constructor
	 */
	public CrawlOrder() {
		stop = Boolean.FALSE;
		wait = 0;
	}

	/**
	 * Constructor with page as a parameter
	 * @param page the page
	 */
	public CrawlOrder(Page page) {
		link = page.getLink();
		stop = Boolean.FALSE;
		wait = 0;
		innerDeep = page.getInnerDeep();
		outerDeep = page.getOuterDeep();
		expectedScore = page.getScore();
	}

	/**
	 * Getter of link of the page
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * Setter of the link of the page
	 * @param link the link
	 */
	public void setLink(Link link) {
		this.link = link;
	}

	/**
	 * Getter of the boolean stop
	 * @return the boolean stop
	 */
	public Boolean getStop() {
		return stop;
	}

	/**
	 * Setter of the boolean stop
	 * @param stop the boolean stop
	 */
	public void setStop(Boolean stop) {
		this.stop = stop;
	}

	/**
	 * Getter of the waiting time
	 * @return the waiting time
	 */
	public Integer getWait() {
		return wait;
	}

	/**
	 * Setter of the waiting time
	 * @param wait the waiting time
	 */
	public void setWait(Integer wait) {
		this.wait = wait;
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
	 * Getter of expected score
	 * @return the expected score
	 */
	public Double getExpectedScore() {
		return expectedScore;
	}

	/**
	 * Setter of expected score
	 * @param expectedScore the expected score
	 */
	public void setExpectedScore(Double expectedScore) {
		this.expectedScore = expectedScore;
	}

}
