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

import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.process.KeywordManager;
import org.mfcrawler.model.util.I18nUtil;

public class ContentAnalysis {

	public static JTable analyse(Page page) {
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
		KeywordManager.countOccurrences(wordsOccurrences, page.getContent());
		return buildTable(wordsOccurrences);
	}

	public static JTable analyse(Site site) {
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();

		// FIXME Continuer ICI

		return buildTable(wordsOccurrences);
	}

	private static JTable buildTable(Map<String, Integer> wordsOccurrences) {
		Object[][] rowData = buildRowData(wordsOccurrences);

		String[] columnNames = new String[4];
		columnNames[0] = I18nUtil.getMessage("overview.detail.analysis.word");
		columnNames[1] = I18nUtil.getMessage("overview.detail.analysis.occurrence");
		columnNames[2] = I18nUtil.getMessage("overview.detail.analysis.weight");
		columnNames[3] = I18nUtil.getMessage("overview.detail.analysis.calculatedScore");

		JTable analysisTable = new JTable(new AnalysisTableModel(rowData, columnNames));
		analysisTable.setAutoCreateRowSorter(true);
		return analysisTable;
	}

	private static Object[][] buildRowData(Map<String, Integer> wordsOccurrences) {
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();
		for (String word : keywordMap.keySet()) {
			if (wordsOccurrences.get(word) == null) {
				wordsOccurrences.put(word, 0);
			}
		}

		int i = 0;
		Set<String> wordSet = wordsOccurrences.keySet();
		Object[][] rowData = new Object[wordSet.size()][4];
		for (String word : wordSet) {
			Integer occurrence = wordsOccurrences.get(word);
			rowData[i][0] = word;
			rowData[i][1] = occurrence;

			Integer weight = keywordMap.get(word);
			if (weight != null) {
				rowData[i][2] = weight;
				rowData[i][3] = KeywordManager.calculate(occurrence, weight);
			}

			i++;
		}

		return rowData;
	}

	private static class AnalysisTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -8836034757791958634L;

		public AnalysisTableModel(Object[][] rowData, String[] columnNames) {
			super(rowData, columnNames);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

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