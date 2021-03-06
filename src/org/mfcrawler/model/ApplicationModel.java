/*
    Mini Focused Crawler : focused web crawler with a simple GUI
    Copyright (C) 2013-2014  lbertelo

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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.mfcrawler.Main;
import org.mfcrawler.model.dao.DbmsManager;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.export.ExportResults;
import org.mfcrawler.model.export.ExportResults.EFormatExport;
import org.mfcrawler.model.export.ExportResults.EScopeExport;
import org.mfcrawler.model.export.config.LoadApplicationConfig;
import org.mfcrawler.model.export.config.LoadCrawlProjectConfig;
import org.mfcrawler.model.pojo.ApplicationConfig;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.process.Supervisor;
import org.mfcrawler.model.process.content.GlobalAnalysis;
import org.mfcrawler.model.process.content.KeywordManager;
import org.mfcrawler.model.process.content.WordAnalysisUtil;
import org.mfcrawler.model.util.I18nUtil;

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
		// "Logger.GLOBAL_LOGGER_NAME" doesn't work so we use ""
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.ALL);
		try {
			FileHandler fileTxt = new FileHandler("crawler-%u-%g.log", 500000, 2);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			fileTxt.setLevel(Level.WARNING);
			logger.addHandler(fileTxt);
		} catch (Exception e) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Logger initialisation error", e);
		}
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
				KeywordManager.setKeywordMap(currentCrawlProject.getKeywordMap());
				dbmsManager.connect(currentCrawlProject.getName(), config.getCacheSizeOfDbms());
				dbmsManager.init();
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
				DbmsManager.get().clearTables();
				ApplicationModel.this.notify(IPropertyName.SITES_PAGES_NUMBER, null);
				ApplicationModel.this.notify(IPropertyName.PROJECT_LOADED, currentCrawlProject);
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
					PageDAO pageDao = new PageDAO();
					
					siteDao.beginTransaction();
					siteDao.initBlacklist();
					for (Domain domain : blacklistDomains) {
						siteDao.updateBlacklist(domain, true);
					}
					pageDao.deleteBlacklistedPages();
					siteDao.endTransaction();
					
					currentCrawlProject.setBlacklistDomains(blacklistDomains);
				}

				// Recalculate score
				if (!keywordMap.equals(currentCrawlProject.getKeywordMap())) {
					ApplicationModel.this.notify(IPropertyName.LOADING,
							I18nUtil.getMessage("loading.recalculateScores.step1"));
					KeywordManager.setKeywordMap(keywordMap);
					KeywordManager.recalculateAllPages(ApplicationModel.this);
					currentCrawlProject.setKeywordMap(keywordMap);
				}

				LoadCrawlProjectConfig.saveCrawlProject(currentCrawlProject);
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();

	}

	// Analyze

	public void analyzeContents() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ApplicationModel.this.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.analyze"));
				List<GlobalAnalysis> analysisData = WordAnalysisUtil.analyze();
				ApplicationModel.this.notify(IPropertyName.CONTENTS_ANALYZED, analysisData);
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();
	}

	// Export

	/**
	 * Export the result (crawled page) to different format and scope
	 * @param file the file containing the export result
	 * @param scope the scope of the export
	 * @param format the format of the export
	 * @param minScoreValue the minimum score value to export
	 */
	public void exportResult(final File file, final EScopeExport scope, final EFormatExport format,
			final Double minScore) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ApplicationModel.this.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.export"));
				ExportResults.export(file, scope, format, minScore);
				ApplicationModel.this.notify(IPropertyName.LOADED, null);
			}
		}, "Loading").start();
	}

}
