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

package org.mfcrawler.model;

import java.util.Map;
import java.util.Set;

import org.mfcrawler.model.dao.DbmsManager;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.pojo.ApplicationConfig;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.process.KeywordManager;
import org.mfcrawler.model.process.Supervisor;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.model.util.file.LoadApplicationConfig;
import org.mfcrawler.model.util.file.LoadCrawlProjectConfig;

/**
 * Model of the application
 * 
 * @author lbertelo
 */
public final class ApplicationModel extends SwingPropertyChangeModel {

	/**
	 * Configuration of the application
	 */
	private static ApplicationConfig config;

	/**
	 * The supervisor
	 */
	private Supervisor supervisor;

	/**
	 * The current crawl project
	 */
	private CrawlProject currentCrawlProject;

	/**
	 * Default constructor
	 */
	public ApplicationModel() {

	}

	// Model

	/**
	 * Getter of supervisor
	 * @return the supervisor
	 */
	public Supervisor getSupervisor() {
		return supervisor;
	}

	/**
	 * Initialize the model (load configuration and crawl project)
	 */
	public void initModel() {
		String defaultProjectName = getConfig().getLastOpenedProject();
		loadCrawlProject(defaultProjectName);
		getConfig().refreshProxy();
	}

	/**
	 * Close the model (close configuration, crawl project and database
	 * connection)
	 */
	public void closeModel() {
		notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.closing"));

		new Thread(new Runnable() {
			@Override
			public void run() {
				getConfig().setLastOpenedProject(currentCrawlProject.getName());
				LoadApplicationConfig.saveApplicationConfig(getConfig());
				LoadCrawlProjectConfig.saveCrawlProject(currentCrawlProject);
				DbmsManager.get().disconnect();
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Closing").start();
	}

	// ApplicationConfig

	/**
	 * Return the unique instance of application configuration
	 * @return the configuration of application
	 */
	public static synchronized ApplicationConfig getConfig() {
		if (config == null) {
			config = LoadApplicationConfig.loadApplicationConfig();
		}
		return config;
	}

	// Project

	/**
	 * Return the current crawl project
	 * @return the current crawl project
	 */
	public CrawlProject getCurrentCrawlProject() {
		return currentCrawlProject;
	}

	/**
	 * Load the crawl project (and initialize the dbmsManager)
	 * @param projectName the name of the crawl project
	 */
	public void loadCrawlProject(final String projectName) {
		notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.loadProject") + " \"" + projectName + "\"");

		new Thread(new Runnable() {
			@Override
			public void run() {
				DbmsManager dbmsManager = DbmsManager.get();

				// Close the current project
				if (currentCrawlProject != null) {
					LoadCrawlProjectConfig.saveCrawlProject(currentCrawlProject);
					dbmsManager.disconnect();
				}

				// Load the new project
				currentCrawlProject = LoadCrawlProjectConfig.loadCrawlProject(projectName);
				KeywordManager.get().setKeywordMap(currentCrawlProject.getKeywordMap());

				dbmsManager.connect(currentCrawlProject.getName());
				if (!dbmsManager.checkTables()) {
					dbmsManager.createTables();
				}
				dbmsManager.testConnection();

				supervisor = new Supervisor(ApplicationModel.this, currentCrawlProject);

				// Notify
				ApplicationModel.this.notify(IPropertyName.PROCESSING, I18nUtil.getMessage("processing.init"));
				ApplicationModel.this.notify(IPropertyName.LAUNCHED_THREADS, 0);
				ApplicationModel.this.notify(IPropertyName.SITES_PAGES_NUMBER, null);
				ApplicationModel.this.notify(IPropertyName.PROJECT_LOADED, currentCrawlProject);
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();
	}

	/**
	 * Delete a crawl project
	 * @param projectName the name of the crawl project
	 */
	public void deleteCrawlProject(final String projectName) {
		notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.deleteProject") + " \"" + projectName + "\"");

		new Thread(new Runnable() {
			@Override
			public void run() {
				LoadCrawlProjectConfig.deleteCrawlProjectFiles(projectName);
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Deleting").start();
	}

	/**
	 * Clear a project (delete all information related to this project in
	 * database)
	 */
	public void clearProject() {
		notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.clearProject"));

		new Thread(new Runnable() {
			@Override
			public void run() {
				DbmsManager.get().dropTables();
				DbmsManager.get().createTables();
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();
	}

	// Config

	/**
	 * Applies crawl configuration
	 * @param crawlConfig the crawl configuration
	 */
	public void applyCrawlConfig(final CrawlConfig crawlConfig) {
		currentCrawlProject.setCrawlConfig(crawlConfig);
		LoadCrawlProjectConfig.saveCrawlConfig(currentCrawlProject.getName(), currentCrawlProject.getCrawlConfig());
		if (supervisor.isStarted()) {
			supervisor.launchCrawlThread();
		}

		supervisor.setRemainingCrawlNumber(crawlConfig.getRemainingCrawlNumber());
	}

	// Filters

	/**
	 * Applies filters (blacklist domains and keyword map)
	 * @param blacklistDomains the blacklist domains
	 * @param keywordMap the keyword map
	 */
	public void applyFilters(final Set<Domain> blacklistDomains, final Map<String, Integer> keywordMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				// Delete blacklist domain
				if (!blacklistDomains.equals(currentCrawlProject.getBlacklistDomains())) {
					ApplicationModel.this.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.reblacklistSites"));

					SiteDAO siteDao = new SiteDAO();
					siteDao.initBlacklist();
					for (Domain domain : blacklistDomains) {
						siteDao.updateBlacklist(domain, true);
					}

					currentCrawlProject.setBlacklistDomains(blacklistDomains);
				}

				// Recalculate score
				if (!keywordMap.equals(currentCrawlProject.getKeywordMap())) {
					ApplicationModel.this.notify(IPropertyName.LOADING,
							I18nUtil.getMessage("loading.recalculateScores"));

					KeywordManager.get().setKeywordMap(keywordMap);
					KeywordManager.get().recalculateAll();

					currentCrawlProject.setKeywordMap(keywordMap);
				}

				LoadCrawlProjectConfig.saveCrawlProject(currentCrawlProject);

				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();

	}

	/**
	 * Add a blacklisted domain to the list
	 * @param domain the new blacklisted domain
	 */
	public void addBlacklistDomain(final Domain domain) {
		SiteDAO siteDao = new SiteDAO();
		siteDao.updateBlacklist(domain, true);

		currentCrawlProject.getBlacklistDomains().add(domain);
		notify(IPropertyName.ADD_BLACKLIST_DOMAIN, domain.getName());
	}

}
