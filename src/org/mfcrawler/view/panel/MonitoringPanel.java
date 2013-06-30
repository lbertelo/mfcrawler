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

package org.mfcrawler.view.panel;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.panel.monitoring.ConfigSubPanel;
import org.mfcrawler.view.panel.monitoring.DetailedMonitorSubPanel;
import org.mfcrawler.view.panel.monitoring.MonitorSubPanel;

/**
 * Panel for monitoring and configuring crawl threads
 * 
 * @author lbertelo
 */
public class MonitoringPanel extends DefaultPanel {

	private ConfigSubPanel configSubPanel;
	private MonitorSubPanel monitorSubPanel;
	private DetailedMonitorSubPanel detailedMonitorSubPanel;

	public MonitoringPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("monitoring.title"), I18nUtil.getMessage("monitoring.description"));

		getModel().addListener(IPropertyName.PROJECT_LOADED, this);
		getModel().addListener(IPropertyName.CRAWL_THREAD_INFO, this);
		getModel().addListener(IPropertyName.PROCESSING, this);
		getModel().addListener(IPropertyName.SITES_PAGES_NUMBER, this);
		getModel().addListener(IPropertyName.REMAINING_CRAWL_NUMBER, this);
		getModel().addListener(IPropertyName.LAUNCHED_THREADS, this);
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		configSubPanel = new ConfigSubPanel(this);
		panel.add(configSubPanel.getPanel());

		monitorSubPanel = new MonitorSubPanel(this);
		panel.add(monitorSubPanel.getPanel());

		detailedMonitorSubPanel = new DetailedMonitorSubPanel(this);
		panel.add(detailedMonitorSubPanel.getPanel());

		return panel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			CrawlProject crawlProject = (CrawlProject) evt.getNewValue();
			configSubPanel.setCrawlConfig(crawlProject.getCrawlConfig());

		} else if (propertyName.equals(IPropertyName.CRAWL_THREAD_INFO)) {
			@SuppressWarnings("unchecked")
			Map<Integer, String> map = (Map<Integer, String>) evt.getNewValue();
			detailedMonitorSubPanel.updateThreadInfo(map);

		} else if (propertyName.equals(IPropertyName.PROCESSING)) {
			String processing = (String) evt.getNewValue();
			monitorSubPanel.updateProcessingInfo(processing);

		} else if (propertyName.equals(IPropertyName.SITES_PAGES_NUMBER)) {
			monitorSubPanel.updateCrawledSitesPages();

		} else if (propertyName.equals(IPropertyName.REMAINING_CRAWL_NUMBER)) {
			Integer remainingCrawlNumber = (Integer) evt.getNewValue();
			configSubPanel.updateRemainingCrawlNumber(remainingCrawlNumber);

		} else if (propertyName.equals(IPropertyName.LAUNCHED_THREADS)) {
			Integer launchedThreads = (Integer) evt.getNewValue();
			monitorSubPanel.updateLaunchedThreads(launchedThreads);

		}
	}

	public void applyCrawlConfig(CrawlConfig crawlConfig) {
		getModel().applyCrawlConfig(crawlConfig);
	}

	public void startCrawl() {
		getModel().getSupervisor().startCrawl();
	}

	public void stopCrawl() {
		getModel().getSupervisor().stopCrawl();
	}

}
