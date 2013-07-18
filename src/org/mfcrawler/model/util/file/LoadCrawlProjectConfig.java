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

package org.mfcrawler.model.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONValue;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.util.ConversionUtils;

/**
 * Allows to load the crawl project (configuration, keywordList and
 * blacklistDomain)
 * 
 * @author lbertelo
 */
public class LoadCrawlProjectConfig {

	/**
	 * Prefix for the project folders
	 */
	private static final String PREFIX_FOLDER = "project_";

	/**
	 * Filename of the crawl configuration
	 */
	private static final String CRAWL_CONFIG_FILENAME = "crawlConfig.json";

	/**
	 * Filename of the blacklist Domain
	 */
	private static final String BLACKLIST_DOMAINS_FILENAME = "blacklistDomains.txt";

	/**
	 * Filename of the keyword list
	 */
	private static final String KEYWORD_LIST_FILENAME = "keywordList.txt";

	/**
	 * Thread number key in json configuration file
	 */
	private static final String CC_THREAD_NUMBER = "threadNumber";

	/**
	 * Inner deep key in json configuration file
	 */
	private static final String CC_INNER_DEEP = "innerDeep";

	/**
	 * Outer deep key in json configuration file
	 */
	private static final String CC_OUTER_DEEP = "outerDeep";

	/**
	 * Remaining crawl number key in json configuration file
	 */
	private static final String CC_REMAINING_CRAWL_NUMBER = "remainingCrawlNumber";

	/**
	 * Minimum score key in json configuration file
	 */
	private static final String CC_MINIMUM_SCORE = "minimumScore";

	/**
	 * Crawl delay key in json configuration file
	 */
	private static final String CC_CRAWL_DELAY = "crawlDelay";

	/**
	 * Force crawl key in json configuration file
	 */
	private static final String CC_FORCE_CRAWL = "forceCrawl";

	/**
	 * Force crawl delay key in json configuration file
	 */
	private static final String CC_FORCE_CRAWL_DELAY = "forceCrawlDelay";

	// Crawl project

	/**
	 * Load the crawl project (configuration, keywordList and blacklistDomain)
	 * @param projectName the name of the project
	 * @return the crawl project
	 */
	public static CrawlProject loadCrawlProject(String projectName) {
		CrawlProject crawlProject = null;
		try {
			File directory = new File(getCompleteFilename(projectName, ""));
			if (directory.exists()) {
				crawlProject = new CrawlProject(projectName, new Date(directory.lastModified()));
				crawlProject.setCrawlConfig(loadCrawlConfig(projectName));
				crawlProject.setBlacklistDomains(loadBlacklistDomains(projectName));
				crawlProject.getKeywordMap().clear();
				crawlProject.getKeywordMap().putAll((loadKeywordList(projectName)));
			} else {
				crawlProject = new CrawlProject(projectName, new Date());
				saveCrawlProject(crawlProject);
			}
		} catch (Exception e) {
			Logger.getLogger(LoadCrawlProjectConfig.class.getName())
					.log(Level.SEVERE, "Error to read crawl project", e);
		}

		return crawlProject;
	}

	/**
	 * Save the crawl project (configuration, keywordList and blacklistDomain)
	 * @param crawlProject the crawl project
	 */
	public static void saveCrawlProject(CrawlProject crawlProject) {
		File directory = new File(getCompleteFilename(crawlProject.getName(), ""));
		if (!directory.exists()) {
			directory.mkdirs();
		}

		saveCrawlConfig(crawlProject.getName(), crawlProject.getCrawlConfig());
		saveBlacklistDomains(crawlProject.getName(), crawlProject.getBlacklistDomains());
		saveKeywordList(crawlProject.getName(), crawlProject.getKeywordMap());
	}

	/**
	 * Get complete filename (path + filename) for a file and a project
	 * @param projectName the name of the project
	 * @param filename the filename
	 * @return the complete filename
	 */
	public static String getCompleteFilename(String projectName, String filename) {
		if (filename.isEmpty()) {
			return PREFIX_FOLDER + projectName + "/";
		} else {
			return PREFIX_FOLDER + projectName + "/" + filename;
		}
	}

	/**
	 * Return the list of the existing crawl projects (the crawl projects are
	 * not loaded, they contain only name and creation date)
	 * @return the list of crawl projects (not loaded)
	 */
	public static List<CrawlProject> getCrawlProjectList() {
		List<CrawlProject> crawlProjectList = new ArrayList<CrawlProject>();
		File rootDirectory = new File(".");

		if (rootDirectory.exists()) {
			File[] fileTab = rootDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(PREFIX_FOLDER);
				}
			});

			for (File file : fileTab) {
				if (file.isDirectory()) {
					String projectName = file.getName().substring(PREFIX_FOLDER.length());
					Date creationDate = new Date(file.lastModified());
					crawlProjectList.add(new CrawlProject(projectName, creationDate));
				}
			}
		}

		return crawlProjectList;
	}

	/**
	 * Delete all the files (and the directory) for a project
	 * @param projectName the name of the project
	 */
	public static void deleteCrawlProjectFiles(String projectName) {
		File projectFolder = new File(getCompleteFilename(projectName, ""));
		for (File file : projectFolder.listFiles()) {
			file.delete();
		}
		projectFolder.delete();
	}

	// Keyword List

	/**
	 * Load the keyword list
	 * @param projectName the name of the project
	 * @return the map of keywords
	 */
	private static Map<String, Integer> loadKeywordList(String projectName) {
		Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		File keywordListFile = new File(getCompleteFilename(projectName, KEYWORD_LIST_FILENAME));
		if (keywordListFile.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(keywordListFile));
				while (bufferedReader.ready()) {
					String line = bufferedReader.readLine();

					int separatorIndex = line.indexOf(":");
					String word = line.substring(separatorIndex + 1).trim().toLowerCase();
					Integer weight = ConversionUtils.toInteger(line.substring(0, separatorIndex));

					keywordMap.put(word, weight);
				}
				bufferedReader.close();
			} catch (Exception e) {
				Logger.getLogger(LoadCrawlProjectConfig.class.getName()).log(Level.SEVERE,
						"Error to read crawl project", e);
			}
		} else {
			saveKeywordList(projectName, keywordMap);
		}
		return keywordMap;
	}

	/**
	 * Save the keyword list
	 * @param projectName the name of the project
	 * @param keywordMap the map of keywords
	 */
	public static void saveKeywordList(String projectName, Map<String, Integer> keywordMap) {
		File keywordListFile = new File(getCompleteFilename(projectName, KEYWORD_LIST_FILENAME));
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(keywordListFile));
			for (String word : keywordMap.keySet()) {
				Integer weight = keywordMap.get(word);
				bufferedWriter.write(weight + ":" + word + "\n");
			}
			bufferedWriter.close();
		} catch (Exception e) {
			Logger.getLogger(LoadCrawlProjectConfig.class.getName())
					.log(Level.SEVERE, "Error to save crawl project", e);
		}
	}

	// Blacklist Domain

	/**
	 * Load the blacklist domains
	 * @param projectName the name of the project
	 * @return the set of blacklisted domains
	 */
	private static Set<Domain> loadBlacklistDomains(String projectName) {
		Set<Domain> blacklistDomains = new HashSet<Domain>();
		File blackListDomainFile = new File(getCompleteFilename(projectName, BLACKLIST_DOMAINS_FILENAME));
		if (blackListDomainFile.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(blackListDomainFile));
				while (bufferedReader.ready()) {
					Domain domain = new Domain(bufferedReader.readLine().trim());
					blacklistDomains.add(domain);
				}
				bufferedReader.close();
			} catch (Exception e) {
				Logger.getLogger(LoadCrawlProjectConfig.class.getName()).log(Level.SEVERE,
						"Error to read crawl project", e);
			}
		} else {
			saveBlacklistDomains(projectName, blacklistDomains);
		}
		return blacklistDomains;
	}

	/**
	 * Save the blacklist domains
	 * @param projectName the name of the project
	 * @param blacklistDomains the set of blacklisted domains
	 */
	public static void saveBlacklistDomains(String projectName, Set<Domain> blacklistDomains) {
		File blackListDomainFile = new File(getCompleteFilename(projectName, BLACKLIST_DOMAINS_FILENAME));
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(blackListDomainFile));
			for (Domain domain : blacklistDomains) {
				bufferedWriter.write(domain.getName() + "\n");
			}
			bufferedWriter.close();
		} catch (Exception e) {
			Logger.getLogger(LoadCrawlProjectConfig.class.getName())
					.log(Level.SEVERE, "Error to save crawl project", e);
		}
	}

	// Crawl config

	/**
	 * Load the crawl configuration
	 * @param projectName the name of the project
	 * @return the crawl configuration
	 */
	@SuppressWarnings("unchecked")
	private static CrawlConfig loadCrawlConfig(String projectName) {
		CrawlConfig crawlConfig = new CrawlConfig();
		File crawlConfigFile = new File(getCompleteFilename(projectName, CRAWL_CONFIG_FILENAME));
		if (crawlConfigFile.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(crawlConfigFile));
				Map<String, Object> jsonMap = (Map<String, Object>) JSONValue.parse(bufferedReader);

				crawlConfig.setThreadNumber(((Number) jsonMap.get(CC_THREAD_NUMBER)).intValue());
				crawlConfig.setInnerDeep(((Number) jsonMap.get(CC_INNER_DEEP)).intValue());
				crawlConfig.setOuterDeep(((Number) jsonMap.get(CC_OUTER_DEEP)).intValue());
				crawlConfig.setRemainingCrawlNumber(((Number) jsonMap.get(CC_REMAINING_CRAWL_NUMBER)).intValue());
				crawlConfig.setMinimumScore(((Number) jsonMap.get(CC_MINIMUM_SCORE)).intValue());
				crawlConfig.setCrawlDelay(((Number) jsonMap.get(CC_CRAWL_DELAY)).intValue());
				crawlConfig.setForceCrawl((Boolean) jsonMap.get(CC_FORCE_CRAWL));
				crawlConfig.setForceCrawlDelay(((Number) jsonMap.get(CC_FORCE_CRAWL_DELAY)).intValue());
			} catch (Exception e) {
				Logger.getLogger(LoadCrawlProjectConfig.class.getName()).log(Level.SEVERE,
						"Error to read crawl project", e);
			}
		} else {
			saveCrawlConfig(projectName, crawlConfig);
		}
		return crawlConfig;
	}

	/**
	 * Save the crawl configuration
	 * @param projectName the name of the project
	 * @param crawlConfig the crawl configuration
	 */
	public static void saveCrawlConfig(String projectName, CrawlConfig crawlConfig) {
		try {
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put(CC_THREAD_NUMBER, crawlConfig.getThreadNumber());
			jsonMap.put(CC_INNER_DEEP, crawlConfig.getInnerDeep());
			jsonMap.put(CC_OUTER_DEEP, crawlConfig.getOuterDeep());
			jsonMap.put(CC_REMAINING_CRAWL_NUMBER, crawlConfig.getRemainingCrawlNumber());
			jsonMap.put(CC_MINIMUM_SCORE, crawlConfig.getMinimumScore());
			jsonMap.put(CC_CRAWL_DELAY, crawlConfig.getCrawlDelay());
			jsonMap.put(CC_FORCE_CRAWL, crawlConfig.getForceCrawl());
			jsonMap.put(CC_FORCE_CRAWL_DELAY, crawlConfig.getForceCrawlDelay());

			File crawlConfigFile = new File(getCompleteFilename(projectName, CRAWL_CONFIG_FILENAME));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(crawlConfigFile));
			bufferedWriter.write(JSONValue.toJSONString(jsonMap));
			bufferedWriter.close();
		} catch (Exception e) {
			Logger.getLogger(LoadCrawlProjectConfig.class.getName())
					.log(Level.SEVERE, "Error to save crawl project", e);
		}
	}

}
