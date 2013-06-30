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

import java.util.Date;

import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Describes a response for the crawl threads
 * 
 * @author lbertelo
 */
public class CrawlResponse {

	/**
	 * Link of the crawled page
	 */
	private Link link;

	/**
	 * Date of the crawl
	 */
	private Date crawlDate;

	/**
	 * Default constructor
	 * @param crawlOrder the crawl order
	 */
	public CrawlResponse(CrawlOrder crawlOrder) {
		link = crawlOrder.getLink();
	}

	/**
	 * Getter of link
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * Setter of link
	 * @param link the link
	 */
	public void setLink(Link link) {
		this.link = link;
	}

	/**
	 * Getter of crawl date
	 * @return the crawl date
	 */
	public Date getCrawlDate() {
		return crawlDate;
	}

	/**
	 * Setter of crawl date
	 * @param crawlDate the crawl date
	 */
	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}

}
