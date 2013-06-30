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

package org.mfcrawler.view.panel.monitoring;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.SiteDAO;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.DefaultSubPanel;
import org.mfcrawler.view.panel.MonitoringPanel;

/**
 * SubPanel for monitoring crawl threads
 * 
 * @author lbertelo
 */
public class MonitorSubPanel extends DefaultSubPanel {

	private MonitoringPanel monitoringPanel;
	private JLabel processingValue;
	private JLabel launchedThreadsValue;
	private JLabel crawledSitesNumber;
	private JLabel crawledPagesNumber;
	private JButton startButton;
	private JButton stopButton;

	public MonitorSubPanel(MonitoringPanel monitoringPanel) {
		super(monitoringPanel, I18nUtil.getMessage("monitoring.monitoring"));
		this.monitoringPanel = monitoringPanel;
	}

	@Override
	protected JPanel buildContent() {
		JPanel tempPanel;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 0));

		JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(0, 2));

		tempPanel = new JPanel(new FlowLayout());
		JLabel processingLabel = new JLabel(I18nUtil.getMessage("monitoring.monitoring.processing"));
		tempPanel.add(processingLabel);
		processingValue = new JLabel("?");
		tempPanel.add(processingValue);
		subPanel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel launchedThreadsLabel = new JLabel(I18nUtil.getMessage("monitoring.monitoring.launchedThreads"));
		tempPanel.add(launchedThreadsLabel);
		launchedThreadsValue = new JLabel("?");
		tempPanel.add(launchedThreadsValue);
		subPanel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel crawledSitesLabel = new JLabel(I18nUtil.getMessage("monitoring.monitoring.crawledSites"));
		tempPanel.add(crawledSitesLabel);
		crawledSitesNumber = new JLabel("?");
		tempPanel.add(crawledSitesNumber);
		subPanel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel crawledPagesLabel = new JLabel(I18nUtil.getMessage("monitoring.monitoring.crawledPages"));
		tempPanel.add(crawledPagesLabel);
		crawledPagesNumber = new JLabel("?");
		tempPanel.add(crawledPagesNumber);
		subPanel.add(tempPanel);

		panel.add(subPanel);

		tempPanel = new JPanel(new FlowLayout());
		startButton = new JButton(I18nUtil.getMessage("general.start"));
		startButton.addActionListener(new startAction());
		tempPanel.add(startButton);
		stopButton = new JButton(I18nUtil.getMessage("general.stop"));
		stopButton.addActionListener(new stopAction());
		tempPanel.add(stopButton);

		panel.add(tempPanel);

		return panel;
	}

	// Update methods

	public void updateProcessingInfo(String processing) {
		processingValue.setText(processing);
	}

	public void updateLaunchedThreads(Integer launchedThreads) {
		launchedThreadsValue.setText(ConversionUtils.toString(launchedThreads));
	}

	public void updateCrawledSitesPages() {
		SiteDAO siteDao = new SiteDAO();
		PageDAO pageDao = new PageDAO();

		crawledSitesNumber.setText(ConversionUtils.toString(siteDao.getCrawledSitesNumber()));
		crawledPagesNumber.setText(ConversionUtils.toString(pageDao.getCrawledPagesNumber()));
	}

	// Listener Classes

	private class startAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			monitoringPanel.startCrawl();
		}
	}

	private class stopAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			monitoringPanel.stopCrawl();
		}
	}

}
