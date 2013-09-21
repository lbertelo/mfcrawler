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

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.process.content.BasicAnalysis;
import org.mfcrawler.model.process.content.PageAnalysis;
import org.mfcrawler.model.process.content.WordAnalysisUtil;
import org.mfcrawler.model.util.I18nUtil;

public class ContentAnalysisPane {

	private JTable analysisTable;
	private int columnNumber;

	public ContentAnalysisPane(Page page) {
		List<BasicAnalysis> analysisData = WordAnalysisUtil.analyze(page);
		columnNumber = 4;
		analysisTable = buildTable(analysisData);
	}

	public ContentAnalysisPane(Site site) {
		List<BasicAnalysis> analysisData = WordAnalysisUtil.analyze(site);
		columnNumber = 3;
		analysisTable = buildTable(analysisData);
	}

	public JTable getTable() {
		return analysisTable;
	}

	private JTable buildTable(List<BasicAnalysis> analysisData) {
		// header
		String[] columnNames = new String[columnNumber];
		columnNames[0] = I18nUtil.getMessage("overview.detail.analysis.word");
		columnNames[1] = I18nUtil.getMessage("overview.detail.analysis.occurrence");
		columnNames[2] = I18nUtil.getMessage("overview.detail.analysis.weight");
		if (columnNumber == 4) {
			columnNames[3] = I18nUtil.getMessage("overview.detail.analysis.score");
		}

		// data
		Object[][] rowData = new Object[analysisData.size()][columnNumber];
		int i = 0;
		for (BasicAnalysis basicAnalysis : analysisData) {
			rowData[i][0] = basicAnalysis.getWord();
			rowData[i][1] = basicAnalysis.getOccurrence();
			rowData[i][2] = basicAnalysis.getWeight();
			if (columnNumber == 4 && basicAnalysis instanceof PageAnalysis) {
				rowData[i][3] = ((PageAnalysis) basicAnalysis).getScore();
			}
			i++;
		}

		// JTable
		JTable analysisTable = new JTable(new AnalysisTableModel(rowData, columnNames));
		analysisTable.setAutoCreateRowSorter(true);

		// default sorter
		analysisTable.getRowSorter().toggleSortOrder(1);
		analysisTable.getRowSorter().toggleSortOrder(1);
		analysisTable.getRowSorter().toggleSortOrder(2);
		analysisTable.getRowSorter().toggleSortOrder(2);

		return analysisTable;
	}

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
			case 1:
			case 2:
				return Integer.class;
			case 3:
				return Long.class;
			default:
				return String.class;
			}
		}
	}

}