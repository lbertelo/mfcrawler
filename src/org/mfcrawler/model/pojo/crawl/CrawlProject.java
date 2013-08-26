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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.util.ConversionUtils;

/**
 * Describes a crawl project
 * 
 * @author lbertelo
 */
public class CrawlProject {

	/**
	 * Name of the crawl project
	 */
	private String name;

	/**
	 * Creation date of the crawl project
	 */
	private Date creationDate;

	/**
	 * Configuration of the crawl project
	 */
	private CrawlConfig crawlConfig;

	/**
	 * Blacklisted domains of the crawl project
	 */
	private Set<Domain> blacklistDomains;

	/**
	 * Keyword map of the crawl project (words with their scores)
	 */
	private Map<String, Integer> keywordMap;

	/**
	 * Default constructor
	 * @param projectName the project name
	 * @param creationDate creation date of the project
	 */
	public CrawlProject(String projectName, Date creationDate) {
		this.name = projectName;
		this.creationDate = creationDate;
		crawlConfig = new CrawlConfig();
		blacklistDomains = new HashSet<Domain>();
		keywordMap = new HashMap<String, Integer>();
	}

	/**
	 * Getter of the project name
	 * @return the name of the project
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter of the project name
	 * @param name the name of the project
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter of the project creation date
	 * @return the creation date of the project
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Setter of the project creation date
	 * @param creationDate the creation date of the project
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Getter of the crawl project config
	 * @return the crawl config of the project
	 */
	public CrawlConfig getCrawlConfig() {
		return crawlConfig;
	}

	/**
	 * Setter of the crawl project config
	 * @param crawlConfig the crawl config of the project
	 */
	public void setCrawlConfig(CrawlConfig crawlConfig) {
		this.crawlConfig = crawlConfig;
	}

	/**
	 * Getter of the blacklist domains
	 * @return the blacklist domains
	 */
	public Set<Domain> getBlacklistDomains() {
		return blacklistDomains;
	}

	/**
	 * Setter of the blacklist domains
	 * @param blacklistDomains the blacklist domains
	 */
	public void setBlacklistDomains(Set<Domain> blacklistDomains) {
		this.blacklistDomains = blacklistDomains;
	}

	/**
	 * Getter of the keyword map
	 * @return the keyword map
	 */
	public Map<String, Integer> getKeywordMap() {
		return keywordMap;
	}

	/**
	 * Setter of the keyword map
	 * @param keywordMap the keyword map
	 */
	public void setKeywordMap(Map<String, Integer> keywordMap) {
		this.keywordMap = keywordMap;
	}

	@Override
	public String toString() {
		return name + " : " + ConversionUtils.toString(creationDate);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CrawlProject) {
			CrawlProject crawlProject = (CrawlProject) object;
			return name.equals(crawlProject.getName());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
