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

package org.mfcrawler.model.pojo.site;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Describes a page identified by a link
 * 
 * @author lbertelo
 */
public class Page {

	/**
	 * Link of the page
	 */
	private Link link; // key

	/**
	 * Content of the page (without html tags)
	 */
	private String content;

	/**
	 * Score of the page (calculated or estimated)
	 */
	private Double score;

	/**
	 * Inner deep is the distance between pages in a same site
	 */
	private Integer innerDeep;

	/**
	 * Outer deep is the distance between sites
	 */
	private Integer outerDeep;

	/**
	 * Time when the page was crawled
	 */
	private Date crawlTime;

	/**
	 * Indicates if the page is allow to crawl (by the robots.txt)
	 */
	private Boolean allowCrawl;

	/**
	 * Indicates if is a redirect page
	 */
	private Boolean redirectPage;

	/**
	 * Indicates is the page is priority to crawl
	 */
	private Boolean crawlNow;

	/**
	 * Error message if the crawl fails
	 */
	private String crawlError;

	/**
	 * List of incoming external links
	 */
	private List<Link> incomingExternLinks;

	/**
	 * List of incoming internal links
	 */
	private List<Link> incomingInternLinks;

	/**
	 * List of outgoing external links
	 */
	private List<Link> outgoingExternLinks;

	/**
	 * List of outgoing internal links
	 */
	private List<Link> outgoingInternLinks;

	/**
	 * Default constructor
	 * @param link the link of the page
	 */
	public Page(Link link) {
		this.link = link;
		incomingExternLinks = new ArrayList<Link>();
		incomingInternLinks = new ArrayList<Link>();
		outgoingExternLinks = new ArrayList<Link>();
		outgoingInternLinks = new ArrayList<Link>();
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
	 * Getter of content
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Setter of content
	 * @param content the content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Getter of score
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Setter of score
	 * @param score the score
	 */
	public void setScore(Double score) {
		this.score = score;
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
	 * Getter of crawl time
	 * @return the crawl time
	 */
	public Date getCrawlTime() {
		return crawlTime;
	}

	/**
	 * Setter of crawl time
	 * @param crawlTime the crawl time
	 */
	public void setCrawlTime(Date crawlTime) {
		this.crawlTime = crawlTime;
	}

	/**
	 * Getter of allow crawl
	 * @return the boolean allow crawl
	 */
	public Boolean getAllowCrawl() {
		return allowCrawl;
	}

	/**
	 * Setter of allow crawl
	 * @param allowCrawl the boolean allow crawl
	 */
	public void setAllowCrawl(Boolean allowCrawl) {
		this.allowCrawl = allowCrawl;
	}

	/**
	 * Getter of redirect page
	 * @return the boolean redirect page
	 */
	public Boolean getRedirectPage() {
		return redirectPage;
	}

	/**
	 * Setter of redirect page
	 * @param redirectPage the boolean redirect page
	 */
	public void setRedirectPage(Boolean redirectPage) {
		this.redirectPage = redirectPage;
	}

	/**
	 * Getter of crawl now
	 * @return the boolean crawl now
	 */
	public Boolean getCrawlNow() {
		return crawlNow;
	}

	/**
	 * Setter of crawl now
	 * @param crawlNow the boolean crawl now
	 */
	public void setCrawlNow(Boolean crawlNow) {
		this.crawlNow = crawlNow;
	}

	/**
	 * Getter of crawl error
	 * @return the crawl error message
	 */
	public String getCrawlError() {
		return crawlError;
	}

	/**
	 * Setter of crawl error
	 * @param crawlError the crawl error message
	 */
	public void setCrawlError(String crawlError) {
		this.crawlError = crawlError;
	}

	/**
	 * Getter of incoming external links
	 * @return the list of incoming external links
	 */
	public List<Link> getIncomingExternLinks() {
		return incomingExternLinks;
	}

	/**
	 * Setter of incoming external links
	 * @param incomingExternLinks the list of incoming external links
	 */
	public void setIncomingExternLinks(List<Link> incomingExternLinks) {
		this.incomingExternLinks = incomingExternLinks;
	}

	/**
	 * Getter of incoming internal links
	 * @return the list of incoming internal links
	 */
	public List<Link> getIncomingInternLinks() {
		return incomingInternLinks;
	}

	/**
	 * Setter of incoming internal links
	 * @param incomingInternLinks the list of incoming internal links
	 */
	public void setIncomingInternLinks(List<Link> incomingInternLinks) {
		this.incomingInternLinks = incomingInternLinks;
	}

	/**
	 * Getter of outgoing external links
	 * @return the list of outgoing external links
	 */
	public List<Link> getOutgoingExternLinks() {
		return outgoingExternLinks;
	}

	/**
	 * Setter of outgoing external links
	 * @param outgoingExternLinks the list of outgoing external links
	 */
	public void setOutgoingExternLinks(List<Link> outgoingExternLinks) {
		this.outgoingExternLinks = outgoingExternLinks;
	}

	/**
	 * Getter of outgoing internal links
	 * @return the list of outgoing internal links
	 */
	public List<Link> getOutgoingInternLinks() {
		return outgoingInternLinks;
	}

	/**
	 * Setter of outgoing internal links
	 * @param outgoingInternLinks the list of outgoing internal links
	 */
	public void setOutgoingInternLinks(List<Link> outgoingInternLinks) {
		this.outgoingInternLinks = outgoingInternLinks;
	}

	@Override
	public String toString() {
		return link.getUrl();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Page) {
			Page page = (Page) object;
			return getLink().equals(page.getLink());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getLink().hashCode();
	}

}
