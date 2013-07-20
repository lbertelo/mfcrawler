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

package org.mfcrawler.view.panel.overview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.mfcrawler.model.pojo.OverviewParams;
import org.mfcrawler.model.pojo.OverviewParams.ESiteOrder;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.panel.OverviewPanel;

/**
 * Panel for refreshing page and site information
 * 
 * @author lbertelo
 */
public class RefreshDetailPanel {

	private static final boolean CRAWLED_DEFAULT = true;
	private static final boolean JUST_FOUND_DEFAULT = false;
	private static final boolean ERROR_DEFAULT = false;
	private static final boolean REDIRECT_PAGE_DEFAULT = false;

	private static final String NAME_ORDER = I18nUtil.getMessage("overview.refreshDetail.name");
	private static final String SCORE_ORDER = I18nUtil.getMessage("overview.refreshDetail.score");
	private static final String DEEP_ORDER = I18nUtil.getMessage("overview.refreshDetail.deep");
	private static final String CRAWLTIME_ORDER = I18nUtil.getMessage("overview.refreshDetail.crawlTime");
	private static final String[] ORDER_LIST_VALUE = { NAME_ORDER, SCORE_ORDER, DEEP_ORDER, CRAWLTIME_ORDER };

	private DetailSubPanel detailSubPanel;
	private JPanel panel;
	private JCheckBox crawledCheckbox;
	private JCheckBox errorCheckbox;
	private JCheckBox redirectPageCheckbox;
	private JCheckBox justFoundCheckbox;
	private JComboBox<String> orderList;
	private JLabel resultPagesNumber;
	private JLabel resultSitesNumber;

	public RefreshDetailPanel(DetailSubPanel detailSubPanel) {
		this.detailSubPanel = detailSubPanel;
		buildContent();
	}

	public JPanel getPanel() {
		return panel;
	}

	private void buildContent() {
		JPanel tempPanel, subTempPanel;
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Font fontBold = ((FontUIResource) UIManager.get("Label.font")).deriveFont(Font.BOLD);

		tempPanel = new JPanel(new GridLayout(0, 2));

		// Refresh params

		JLabel displayLabel = new JLabel(I18nUtil.getMessage("overview.refreshDetail.display"));
		displayLabel.setFont(fontBold);
		subTempPanel = new JPanel(new FlowLayout());
		subTempPanel.add(displayLabel);
		tempPanel.add(subTempPanel);

		crawledCheckbox = new JCheckBox(I18nUtil.getMessage("overview.refreshDetail.crawled"), CRAWLED_DEFAULT);
		tempPanel.add(crawledCheckbox);
		justFoundCheckbox = new JCheckBox(I18nUtil.getMessage("overview.refreshDetail.justFound"), JUST_FOUND_DEFAULT);
		tempPanel.add(new JLabel());
		tempPanel.add(justFoundCheckbox);
		errorCheckbox = new JCheckBox(I18nUtil.getMessage("overview.refreshDetail.error"), ERROR_DEFAULT);
		tempPanel.add(new JLabel());
		tempPanel.add(errorCheckbox);
		redirectPageCheckbox = new JCheckBox(I18nUtil.getMessage("overview.refreshDetail.redirectPage"),
				REDIRECT_PAGE_DEFAULT);
		tempPanel.add(new JLabel());
		tempPanel.add(redirectPageCheckbox);

		tempPanel.add(new JLabel());
		tempPanel.add(new JLabel());

		JLabel orderLabel = new JLabel(I18nUtil.getMessage("overview.refreshDetail.order"));
		orderLabel.setFont(fontBold);
		subTempPanel = new JPanel(new FlowLayout());
		subTempPanel.add(orderLabel);
		tempPanel.add(subTempPanel);

		orderList = new JComboBox<String>(ORDER_LIST_VALUE);
		subTempPanel = new JPanel(new BorderLayout());
		subTempPanel.add(orderList, BorderLayout.LINE_START);
		tempPanel.add(subTempPanel);

		tempPanel.add(new JLabel());
		tempPanel.add(new JLabel());

		// Display number of results

		JLabel resultSitesLabel = new JLabel(I18nUtil.getMessage("overview.refreshDetail.resultSitesNumber"));
		resultSitesLabel.setFont(fontBold);
		subTempPanel = new JPanel(new FlowLayout());
		subTempPanel.add(resultSitesLabel);
		tempPanel.add(subTempPanel);

		resultSitesNumber = new JLabel("?");
		subTempPanel = new JPanel(new BorderLayout());
		subTempPanel.add(resultSitesNumber, BorderLayout.LINE_START);
		tempPanel.add(subTempPanel);

		JLabel resultPagesLabel = new JLabel(I18nUtil.getMessage("overview.refreshDetail.resultPagesNumber"));
		resultPagesLabel.setFont(fontBold);
		subTempPanel = new JPanel(new FlowLayout());
		subTempPanel.add(resultPagesLabel);
		tempPanel.add(subTempPanel);

		resultPagesNumber = new JLabel("?");
		subTempPanel = new JPanel(new BorderLayout());
		subTempPanel.add(resultPagesNumber, BorderLayout.LINE_START);
		tempPanel.add(subTempPanel);

		tempPanel.add(new JLabel());
		tempPanel.add(new JLabel());

		panel.add(tempPanel, BorderLayout.PAGE_START);

		// Button

		JButton refreshButton = new JButton(I18nUtil.getMessage("general.refresh"));
		refreshButton.addActionListener(new RefreshAction());
		tempPanel = new JPanel(new FlowLayout());
		tempPanel.add(refreshButton);
		panel.add(tempPanel, BorderLayout.CENTER);
	}

	public OverviewParams getParams() {
		OverviewParams params = new OverviewParams();
		params.setSelectCrawled(crawledCheckbox.isSelected());
		params.setSelectJustFound(justFoundCheckbox.isSelected());
		params.setSelectError(errorCheckbox.isSelected());
		params.setSelectRedirectPage(redirectPageCheckbox.isSelected());

		String orderSelect = (String) orderList.getSelectedItem();
		if (orderSelect.equals(NAME_ORDER)) {
			params.setOrder(ESiteOrder.NAME);
		} else if (orderSelect.equals(SCORE_ORDER)) {
			params.setOrder(ESiteOrder.SCORE);
		} else if (orderSelect.equals(DEEP_ORDER)) {
			params.setOrder(ESiteOrder.DEEP);
		} else if (orderSelect.equals(CRAWLTIME_ORDER)) {
			params.setOrder(ESiteOrder.CRAWLTIME);
		}

		return params;
	}

	public void updateResultsNumber(Integer sitesNumber, Integer pagesNumber) {
		resultSitesNumber.setText(ConversionUtils.toString(sitesNumber));
		resultPagesNumber.setText(ConversionUtils.toString(pagesNumber));
	}

	// Listener Classes

	private class RefreshAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			((OverviewPanel) detailSubPanel.getParentPanel()).refresh();
		}
	}

}
