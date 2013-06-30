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

import org.mfcrawler.model.pojo.site.link.Domain;

/**
 * Describes a site (website) identified by a domain
 * 
 * @author lbertelo
 */
public class Site {

	/**
	 * Domain of the site
	 */
	private Domain domain; // key
	
	/**
	 * Indicates if the site is blacklisted
	 */
	private Boolean blacklisted;
	
	/**
	 * Indicates if the robots.txt exists
	 */
	private Boolean robotFileExist;
	
	/**
	 * List of robotPath from robots.txt
	 */
	private List<RobotPath> robotPathList;
	
	/**
	 * Crawl delay from robots.txt
	 */
	private Integer robotCrawlDelay;
	
	/**
	 * List of incoming domains
	 */
	private List<Domain> incomingDomains;
	
	/**
	 * List of outgoing domains
	 */
	private List<Domain> outgoingDomains;
	
	/**
	 * Minimum outer deep of the site
	 */
	private Integer minOuterDeep;
	
	/**
	 * Number of crawled pages
	 */
	private Integer crawledPagesNumber;
	
	/**
	 * Sum of the pages' score
	 */
	private Integer totalScore;
	
	/**
	 * Time of the crawl (crawl of robots.txt)
	 */
	private Date crawlTime;

	/**
	 * Default constructor
	 * @param domain the domain of the site
	 */
	public Site (Domain domain) {
		this.domain = domain;
		robotPathList = new ArrayList<RobotPath>();
		incomingDomains = new ArrayList<Domain>();
		outgoingDomains = new ArrayList<Domain>();
	}

	/**
	 * Getter of domain
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Setter of domain
	 * @param domain the domain
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Getter of blacklisted
	 * @return the boolean blacklisted
	 */
	public Boolean getBlacklisted() {
		return blacklisted;
	}

	/**
	 * Setter of blacklisted
	 * @param blacklisted the boolean blacklisted
	 */
	public void setBlacklisted(Boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

	/**
	 * Getter of robotFileExist
	 * @return the boolean robotFileExist
	 */
	public Boolean getRobotFileExist() {
		return robotFileExist;
	}

	/**
	 * Setter of robotFileExist
	 * @param robotFileExist the boolean robotFileExist
	 */
	public void setRobotFileExist(Boolean robotFileExist) {
		this.robotFileExist = robotFileExist;
	}

	/**
	 * Getter of robotPathList
	 * @return the list of robotPaths
	 */
	public List<RobotPath> getRobotPathList() {
		return robotPathList;
	}

	/**
	 * Setter of robotPathList
	 * @param robotPathList the list of robotPaths
	 */
	public void setRobotPathList(List<RobotPath> robotPathList) {
		this.robotPathList = robotPathList;
	}

	/**
	 * Getter of robot crawl delay
	 * @return the robot crawl delay
	 */
	public Integer getRobotCrawlDelay() {
		return robotCrawlDelay;
	}

	/**
	 * Setter of robot crawl delay
	 * @param robotCrawlDelay the robot crawl delay
	 */
	public void setRobotCrawlDelay(Integer robotCrawlDelay) {
		this.robotCrawlDelay = robotCrawlDelay;
	}

	/**
	 * Getter of incoming domains
	 * @return the list of incoming domains
	 */
	public List<Domain> getIncomingDomains() {
		return incomingDomains;
	}

	/**
	 * Setter of incoming domains
	 * @param incomingDomains the list of incoming domains
	 */
	public void setIncomingDomains(List<Domain> incomingDomains) {
		this.incomingDomains = incomingDomains;
	}

	/**
	 * Getter of outgoing domains
	 * @return the list of outgoing domains
	 */
	public List<Domain> getOutgoingDomains() {
		return outgoingDomains;
	}

	/**
	 * Setter of outgoing domains
	 * @param outgoingDomains the list of outgoing domains
	 */
	public void setOutgoingDomains(List<Domain> outgoingDomains) {
		this.outgoingDomains = outgoingDomains;
	}

	/**
	 * Getter of minimum outer deep
	 * @return the minimum outer deep
	 */
	public Integer getMinOuterDeep() {
		return minOuterDeep;
	}

	/**
	 * Setter of minimum outer deep
	 * @param minOuterDeep the minimum outer deep
	 */
	public void setMinOuterDeep(Integer minOuterDeep) {
		this.minOuterDeep = minOuterDeep;
	}
	
	/**
	 * Getter of crawled pages number
	 * @return the number of crawled pages
	 */
	public Integer getCrawledPagesNumber() {
		return crawledPagesNumber;
	}

	/**
	 * Setter of crawled pages number
	 * @param crawledPagesNumber the number of crawled pages
	 */
	public void setCrawledPagesNumber(Integer crawledPagesNumber) {
		this.crawledPagesNumber = crawledPagesNumber;
	}

	/**
	 * Getter of total score
	 * @return the total score
	 */
	public Integer getTotalScore() {
		return totalScore;
	}

	/**
	 * Setter of total score
	 * @param totalScore the total score
	 */
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
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

	@Override
	public boolean equals(Object object) {
		if (object instanceof Site) {
			Site site = (Site) object;
			return getDomain().equals(site.getDomain());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return domain.toString();
	}

}
