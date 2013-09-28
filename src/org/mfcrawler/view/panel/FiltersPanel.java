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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.export.config.LoadCrawlProjectConfig;
import org.mfcrawler.model.pojo.crawl.CrawlProject;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;
import org.mfcrawler.view.panel.filters.BlackListSubPanel;
import org.mfcrawler.view.panel.filters.KeywordSubPanel;

/**
 * Panel for filtering information (keywords and blacklisted domains)
 * 
 * @author lbertelo
 */
public class FiltersPanel extends DefaultPanel {

	private JPanel panel;
	private KeywordSubPanel keywordSubPanel;
	private BlackListSubPanel blackListSubPanel;
	private JFileChooser fileChooser;
	private JButton applyButton;

	public FiltersPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("filters.title"), I18nUtil.getMessage("filters.description"));
		getModel().addListener(IPropertyName.PROJECT_LOADED, this);
		getModel().addListener(IPropertyName.ADD_BLACKLIST_DOMAIN, this);
	}

	@Override
	protected JComponent buildContent() {
		fileChooser = new JFileChooser();
		panel = new JPanel(new BorderLayout());

		JPanel filtersPanel = new JPanel();
		filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.PAGE_AXIS));

		keywordSubPanel = new KeywordSubPanel(this);
		filtersPanel.add(keywordSubPanel.getPanel());

		blackListSubPanel = new BlackListSubPanel(this);
		filtersPanel.add(blackListSubPanel.getPanel());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton importButton = new JButton(I18nUtil.getMessage("filters.import"));
		importButton.addActionListener(new ImportFiltersAction());
		buttonPanel.add(importButton);
		applyButton = new JButton(I18nUtil.getMessage("filters.apply"));
		applyButton.addActionListener(new ApplyFiltersAction());
		applyButton.setEnabled(false);
		buttonPanel.add(applyButton);
		JButton exportButton = new JButton(I18nUtil.getMessage("filters.export"));
		exportButton.addActionListener(new ExportFiltersAction());
		buttonPanel.add(exportButton);

		panel.add(filtersPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.PAGE_END);

		return panel;
	}

	public void enabledApplyButton(boolean enabled) {
		applyButton.setEnabled(enabled);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			CrawlProject crawlProject = (CrawlProject) evt.getNewValue();
			blackListSubPanel.initBlacklistDomains(crawlProject.getBlacklistDomains());
			keywordSubPanel.initKeywordMap(crawlProject.getKeywordMap());

		} else if (propertyName.equals(IPropertyName.ADD_BLACKLIST_DOMAIN)) {
			Domain domain = (Domain) evt.getNewValue();
			blackListSubPanel.addBlacklistDomain(domain);
		}
	}

	// Listener Classes

	private class ImportFiltersAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showOpenDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File filtersFile = fileChooser.getSelectedFile();
				Map<String, Integer> keywordMap = new HashMap<String, Integer>();
				Set<Domain> blacklistDomains = new HashSet<Domain>();

				LoadCrawlProjectConfig.loadFilters(filtersFile, keywordMap, blacklistDomains);
				for (String word : keywordMap.keySet()) {
					keywordSubPanel.addKeyword(word, keywordMap.get(word));
				}
				for (Domain domain : blacklistDomains) {
					blackListSubPanel.addBlacklistDomain(domain);
				}
			}
		}
	}

	private class ApplyFiltersAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (getModel().getSupervisor().isStopped()) {
				getModel().applyFilters(blackListSubPanel.getBlacklistDomains(), keywordSubPanel.getKeywordMap());
				applyButton.setEnabled(false);
			} else {
				getView().notifyDialog(IPropertyName.WARNING, I18nUtil.getMessage("warning.stopCrawl"));
			}
		}
	}

	private class ExportFiltersAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showOpenDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File filtersFile = fileChooser.getSelectedFile();
				LoadCrawlProjectConfig.saveFilters(filtersFile, keywordSubPanel.getKeywordMap(),
						blackListSubPanel.getBlacklistDomains());
			}
		}
	}

}