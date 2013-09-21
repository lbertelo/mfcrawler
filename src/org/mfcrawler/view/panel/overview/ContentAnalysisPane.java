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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.process.KeywordManager;
import org.mfcrawler.model.util.I18nUtil;

public class ContentAnalysisPane {

	private JTable analysisTable;
	private int columnNumber;

	public ContentAnalysisPane(Page page) {
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();

		KeywordManager.countOccurrences(wordsOccurrences, page.getContent(), keywordMap.keySet());

		columnNumber = 4;
		analysisTable = buildTable(wordsOccurrences, keywordMap);
	}

	public ContentAnalysisPane(Site site) {
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();

		PageDAO pageDao = new PageDAO();
		PageDbIterator pageDbIterator = pageDao.getPagesWithContent(site.getDomain());
		while (pageDbIterator.hasNext()) {
			Page page = pageDbIterator.next();
			KeywordManager.countOccurrences(wordsOccurrences, page.getContent(), keywordMap.keySet());
		}

		columnNumber = 3;
		analysisTable = buildTable(wordsOccurrences, keywordMap);
	}

	public JTable getTable() {
		return analysisTable;
	}

	private JTable buildTable(Map<String, Integer> wordsOccurrences, Map<String, Integer> keywordMap) {
		Object[][] rowData = buildRowData(wordsOccurrences, keywordMap);

		String[] columnNames = new String[columnNumber];
		columnNames[0] = I18nUtil.getMessage("overview.detail.analysis.word");
		columnNames[1] = I18nUtil.getMessage("overview.detail.analysis.occurrence");
		columnNames[2] = I18nUtil.getMessage("overview.detail.analysis.weight");
		if (columnNumber == 4) {
			columnNames[3] = I18nUtil.getMessage("overview.detail.analysis.score");
		}

		JTable analysisTable = new JTable(new AnalysisTableModel(rowData, columnNames));
		analysisTable.setAutoCreateRowSorter(true);
		// default sorter
		analysisTable.getRowSorter().toggleSortOrder(1);
		analysisTable.getRowSorter().toggleSortOrder(1);
		analysisTable.getRowSorter().toggleSortOrder(2);
		analysisTable.getRowSorter().toggleSortOrder(2);

		return analysisTable;
	}

	private Object[][] buildRowData(Map<String, Integer> wordsOccurrences, Map<String, Integer> keywordMap) {
		for (String word : keywordMap.keySet()) {
			if (wordsOccurrences.get(word) == null) {
				wordsOccurrences.put(word, 0);
			}
		}

		int i = 0;
		Set<String> wordSet = wordsOccurrences.keySet();
		Object[][] rowData = new Object[wordSet.size()][columnNumber];
		for (String word : wordSet) {
			Integer occurrence = wordsOccurrences.get(word);
			Integer weight = keywordMap.get(word);

			rowData[i][0] = word;
			rowData[i][1] = occurrence;
			rowData[i][2] = weight;
			if (columnNumber == 4 && weight != null) {
				rowData[i][3] = KeywordManager.calculate(occurrence, weight);
			}

			i++;
		}

		return rowData;
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
				return Double.class;
			default:
				return String.class;
			}
		}
	}

}