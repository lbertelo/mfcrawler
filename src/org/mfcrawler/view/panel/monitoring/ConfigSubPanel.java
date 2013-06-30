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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.pojo.crawl.CrawlConfig;
import org.mfcrawler.model.pojo.crawl.ICrawlConfigParams;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.dialog.StartingPagesDialog;
import org.mfcrawler.view.panel.DefaultSubPanel;
import org.mfcrawler.view.panel.MonitoringPanel;

/**
 * SubPanel for crawl configuration
 * 
 * @author lbertelo
 */
public class ConfigSubPanel extends DefaultSubPanel implements ICrawlConfigParams {

	private MonitoringPanel monitoringPanel;
	private JSpinner threadNumberSpinner;
	private JSpinner innerDeepSpinner;
	private JSpinner outerDeepSpinner;
	private JSpinner crawlNumberSpinner;
	private JSpinner minimumScoreSpinner;
	private JSpinner crawlDelaySpinner;
	private JCheckBox forceCrawlCheckbox;
	private JPanel forceCrawlDelayPanel;
	private JSpinner forceCrawlDelaySpinner;
	private JButton startingPagesButton;
	private JButton applyButton;

	public ConfigSubPanel(MonitoringPanel monitoringPanel) {
		super(monitoringPanel, I18nUtil.getMessage("monitoring.crawlConfig"));
		this.monitoringPanel = monitoringPanel;
	}

	@Override
	protected JPanel buildContent() {
		JPanel tempPanel;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));

		tempPanel = new JPanel(new FlowLayout());
		JLabel threadNumberLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.threadNumber"));
		tempPanel.add(threadNumberLabel);
		threadNumberSpinner = new JSpinner(new SpinnerNumberModel(THREAD_NUMBER_DEFAULT, THREAD_NUMBER_MIN,
				THREAD_NUMBER_MAX, SPINNER_STEP));
		tempPanel.add(threadNumberSpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel innerDeepLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.innerDeep"));
		tempPanel.add(innerDeepLabel);
		innerDeepSpinner = new JSpinner(new SpinnerNumberModel(INNER_DEEP_DEFAULT, INNER_DEEP_MIN, INNER_DEEP_MAX,
				SPINNER_STEP));
		tempPanel.add(innerDeepSpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel outerDeepLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.outerDeep"));
		tempPanel.add(outerDeepLabel);
		outerDeepSpinner = new JSpinner(new SpinnerNumberModel(OUTER_DEEP_DEFAULT, OUTER_DEEP_MIN, OUTER_DEEP_MAX,
				SPINNER_STEP));
		tempPanel.add(outerDeepSpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel crawlNumberLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.crawlNumber"));
		tempPanel.add(crawlNumberLabel);
		crawlNumberSpinner = new JSpinner(new SpinnerNumberModel(REMAINING_CRAWL_NUMBER_DEFAULT,
				REMAINING_CRAWL_NUMBER_MIN, REMAINING_CRAWL_NUMBER_MAX, SPINNER_STEP));
		tempPanel.add(crawlNumberSpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel minimumScoreLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.minimumScore"));
		tempPanel.add(minimumScoreLabel);
		minimumScoreSpinner = new JSpinner(new SpinnerNumberModel(MINIMUM_SCORE_DEFAULT, MINIMUM_SCORE_MIN,
				MINIMUM_SCORE_MAX, SPINNER_STEP));
		tempPanel.add(minimumScoreSpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		JLabel crawlDelayLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.crawlDelay"));
		tempPanel.add(crawlDelayLabel);
		crawlDelaySpinner = new JSpinner(new SpinnerNumberModel(CRAWL_DELAY_DEFAULT, CRAWL_DELAY_MIN, CRAWL_DELAY_MAX,
				SPINNER_STEP));
		tempPanel.add(crawlDelaySpinner);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		forceCrawlCheckbox = new JCheckBox(I18nUtil.getMessage("monitoring.crawlConfig.forceCrawl"));
		forceCrawlCheckbox.addActionListener(new robotForceCrawlAction());
		tempPanel.add(forceCrawlCheckbox);
		panel.add(tempPanel);

		forceCrawlDelayPanel = new JPanel(new FlowLayout());
		forceCrawlDelayPanel.setVisible(false);
		JLabel forceCrawlDelayLabel = new JLabel(I18nUtil.getMessage("monitoring.crawlConfig.forceCrawlDelay"));
		forceCrawlDelayPanel.add(forceCrawlDelayLabel);
		forceCrawlDelaySpinner = new JSpinner(new SpinnerNumberModel(FORCE_CRAWL_DELAY_DEFAULT, FORCE_CRAWL_DELAY_MIN,
				CRAWL_DELAY_MAX, SPINNER_STEP));
		forceCrawlDelayPanel.add(forceCrawlDelaySpinner);
		panel.add(forceCrawlDelayPanel);

		tempPanel = new JPanel(new FlowLayout());
		startingPagesButton = new JButton(I18nUtil.getMessage("monitoring.crawlConfig.startingPages"));
		startingPagesButton.addActionListener(new StartingPagesAction());
		tempPanel.add(startingPagesButton);
		panel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout());
		applyButton = new JButton(I18nUtil.getMessage("monitoring.crawlConfig.apply"));
		applyButton.addActionListener(new ApplyAction());
		tempPanel.add(applyButton);
		panel.add(tempPanel);

		return panel;
	}

	public CrawlConfig getCrawlConfig() {
		CrawlConfig crawlConfig = new CrawlConfig();
		crawlConfig.setThreadNumber(ConversionUtils.toInteger(threadNumberSpinner.getValue()));
		crawlConfig.setInnerDeep(ConversionUtils.toInteger(innerDeepSpinner.getValue()));
		crawlConfig.setOuterDeep(ConversionUtils.toInteger(outerDeepSpinner.getValue()));
		crawlConfig.setRemainingCrawlNumber(ConversionUtils.toInteger(crawlNumberSpinner.getValue()));
		crawlConfig.setMinimumScore(ConversionUtils.toInteger(minimumScoreSpinner.getValue()));
		crawlConfig.setCrawlDelay(ConversionUtils.toInteger(crawlDelaySpinner.getValue()));
		crawlConfig.setForceCrawl(forceCrawlCheckbox.isSelected());
		crawlConfig.setForceCrawlDelay(ConversionUtils.toInteger(forceCrawlDelaySpinner.getValue()));
		return crawlConfig;
	}

	public void setCrawlConfig(CrawlConfig crawlConfig) {
		try {
			threadNumberSpinner.setValue(crawlConfig.getThreadNumber());
			innerDeepSpinner.setValue(crawlConfig.getInnerDeep());
			outerDeepSpinner.setValue(crawlConfig.getOuterDeep());
			crawlNumberSpinner.setValue(crawlConfig.getRemainingCrawlNumber());
			minimumScoreSpinner.setValue(crawlConfig.getMinimumScore());
			crawlDelaySpinner.setValue(crawlConfig.getCrawlDelay());
			forceCrawlCheckbox.setSelected(crawlConfig.getForceCrawl());
			forceCrawlDelayPanel.setVisible(crawlConfig.getForceCrawl());
			forceCrawlDelaySpinner.setValue(crawlConfig.getForceCrawlDelay());
		} catch (Exception e) {
			getView().notifyDialog(IPropertyName.ERROR, I18nUtil.getMessage("error.crawlConfig"));
			Logger.getLogger(ConfigSubPanel.class.getName()).log(Level.SEVERE, "Error to set the crawl config", e);
		}
	}

	public void updateRemainingCrawlNumber(Integer remainingCrawlNumber) {
		crawlNumberSpinner.setValue(remainingCrawlNumber);
	}

	// Listener Classes

	private class ApplyAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			monitoringPanel.applyCrawlConfig(getCrawlConfig());
		}
	}

	private class StartingPagesAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			StartingPagesDialog startingPagesDialog = new StartingPagesDialog(getView(), getView().getModel());
			startingPagesDialog.display();
		}
	}

	private class robotForceCrawlAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			forceCrawlDelayPanel.setVisible(forceCrawlCheckbox.isSelected());
		}
	}

}
