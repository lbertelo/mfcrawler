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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.process.content.GlobalAnalysis;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Panel for global analysis
 * 
 * @author lbertelo
 */
public class AnalyzePanel extends DefaultPanel {

	private JPanel panel;
	private JPanel buttonPanel;
	private JButton analysisButton;

	public AnalyzePanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("analyze.title"), I18nUtil.getMessage("analyze.description"));
		getModel().addListener(IPropertyName.PROJECT_LOADED, this);
		getModel().addListener(IPropertyName.CONTENTS_ANALYZED, this);
	}

	@Override
	protected JComponent buildContent() {
		panel = new JPanel(new BorderLayout());

		buttonPanel = new JPanel(new FlowLayout());
		analysisButton = new JButton(I18nUtil.getMessage("analyze.launchAnalysis"));
		analysisButton.addActionListener(new AnalyzeAction());
		buttonPanel.add(analysisButton);
		panel.add(buttonPanel, BorderLayout.PAGE_START);

		return panel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(IPropertyName.PROJECT_LOADED)) {
			analysisButton.setText(I18nUtil.getMessage("analyze.launchAnalysis"));
			panel.removeAll();
			panel.add(buttonPanel, BorderLayout.PAGE_START);
			panel.add(new JPanel(), BorderLayout.CENTER);

		} else if (propertyName.equals(IPropertyName.CONTENTS_ANALYZED)) {
			@SuppressWarnings("unchecked")
			List<GlobalAnalysis> analysisData = (List<GlobalAnalysis>) evt.getNewValue();
			setAnalysisData(analysisData);
		}
	}

	private void setAnalysisData(List<GlobalAnalysis> analysisData) {
		// header
		String[] columnNames = new String[5];
		columnNames[0] = I18nUtil.getMessage("analyze.table.word");
		columnNames[1] = I18nUtil.getMessage("analyze.table.weightedTfIdf");
		columnNames[2] = I18nUtil.getMessage("analyze.table.tfIdf");
		columnNames[3] = I18nUtil.getMessage("analyze.table.wordFrequency");
		columnNames[4] = I18nUtil.getMessage("analyze.table.docFrequency");

		// data
		Object[][] rowData = new Object[analysisData.size()][5];
		int i = 0;
		for (GlobalAnalysis globalAnalysis : analysisData) {
			rowData[i][0] = globalAnalysis.getWord();
			rowData[i][1] = 1000.0 * globalAnalysis.getWeightedTfIdf();
			rowData[i][2] = 1000.0 * globalAnalysis.getTfIdf();
			rowData[i][3] = 1000.0 * globalAnalysis.getWordFrequency();
			rowData[i][4] = 1000.0 * globalAnalysis.getDocFrequency();
			i++;
		}

		// JTable
		JTable analysisTable = new JTable(new AnalysisTableModel(rowData, columnNames));
		analysisTable.setAutoCreateRowSorter(true);

		// default sorter
		analysisTable.getRowSorter().toggleSortOrder(2);
		analysisTable.getRowSorter().toggleSortOrder(2);
		analysisTable.getRowSorter().toggleSortOrder(1);
		analysisTable.getRowSorter().toggleSortOrder(1);

		analysisButton.setText(I18nUtil.getMessage("analyze.refreshAnalysis"));
		JScrollPane analysisScrollPane = new JScrollPane(analysisTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		panel.removeAll();
		panel.add(buttonPanel, BorderLayout.PAGE_START);
		panel.add(analysisScrollPane, BorderLayout.CENTER);
	}

	// Listener Class

	private class AnalyzeAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			getModel().analyzeContents();
		}
	}

	// TableModel Class

	private class AnalysisTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -8836034757791958634L;

		public AnalysisTableModel(Object[][] rowData, String[] columnNames) {
			super(rowData, columnNames);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return String.class;
			default:
				return Double.class;
			}
		}
	}

}
