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

package org.mfcrawler.view.panel.filters;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.SortedListModel;
import org.mfcrawler.view.panel.DefaultPanel;
import org.mfcrawler.view.panel.DefaultSubPanel;



public class KeywordSubPanel extends DefaultSubPanel implements IFiltersParams {

	private JPanel panel;
	private JTextField wordTextField;
	private JSpinner weightSpinner;
	private JButton addButton;
	private JButton deleteButton;
	private Map<String, Integer> keywordMap;
	private JList<KeywordElement> keywordJList;
	private SortedListModel<KeywordElement> keywordListModel;

	public KeywordSubPanel(DefaultPanel parentPanel) {
		super(parentPanel, I18nUtil.getMessage("filters.keywords"));
		keywordMap = new HashMap<String, Integer>();
	}
	
	public void setKeywordMap(Map<String, Integer> keywordMap) {
		this.keywordMap = new HashMap<String, Integer>(keywordMap);
		
		keywordListModel = new SortedListModel<KeywordElement>();
		for (String word : keywordMap.keySet()) {
			KeywordElement keyword = new KeywordElement();
			keyword.word = word;
			keyword.weight = keywordMap.get(word);
			keywordListModel.addElement(keyword);
		}
		keywordJList.setModel(keywordListModel);
	}
	
	public Map<String, Integer> getKeywordMap() {
		return new HashMap<String, Integer>(keywordMap);
	}

	@Override
	protected JPanel buildContent() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout(10, 10));

		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		JLabel keywordLabel = new JLabel(I18nUtil.getMessage("filters.keywords.keyword"));
		tempPanel.add(keywordLabel);

		wordTextField = new JTextField("", DEFAULT_KEYWORD_COLUMNS);
		wordTextField.addActionListener(new AddAction());
		tempPanel.add(wordTextField);

		JLabel weightLabel = new JLabel(I18nUtil.getMessage("filters.keywords.weight"));
		tempPanel.add(weightLabel);

		weightSpinner = new JSpinner(new SpinnerNumberModel(KEYWORD_NUMBER_DEFAULT, KEYWORD_NUMBER_MIN,
				KEYWORD_NUMBER_MAX, KEYWORD_SPINNER_STEP));
		tempPanel.add(weightSpinner);

		addButton = new JButton(I18nUtil.getMessage("general.add") + " / " + I18nUtil.getMessage("general.update"));
		addButton.addActionListener(new AddAction());
		tempPanel.add(addButton);

		deleteButton = new JButton(I18nUtil.getMessage("general.delete"));
		deleteButton.addActionListener(new DeleteAction());
		deleteButton.setEnabled(false);
		tempPanel.add(deleteButton);

		panel.add(tempPanel, BorderLayout.PAGE_START);

		keywordJList = new JList<KeywordElement>();
		keywordJList.addListSelectionListener(new SelectedValueAction());
		JScrollPane tmp_scroll = new JScrollPane(keywordJList);
		panel.add(tmp_scroll, BorderLayout.CENTER);

		return panel;
	}

	// Class which represents a element in my JList
	
	private class KeywordElement implements Comparable<KeywordElement> {
		public String word;
		public Integer weight;

		@Override
		public String toString() {
			return "\"" + word + "\"  ==>  " + weight + "%";
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof KeywordElement) {
				return word.equals(((KeywordElement) object).word);
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(KeywordElement keyword) {
			return word.compareTo(keyword.word);
		}
	}

	// Listener Classes
	
	private class SelectedValueAction implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (keywordJList.getSelectedValue() instanceof KeywordElement) {
				KeywordElement keywordSelected = keywordJList.getSelectedValue();
				deleteButton.setEnabled(true);
				wordTextField.setText(keywordSelected.word);
				weightSpinner.setValue(keywordSelected.weight);
			} else {
				deleteButton.setEnabled(false);
				wordTextField.setText("");
				weightSpinner.setValue(DEFAULT_WEIGHT_VALUE);
			}
		}
	}

	private class AddAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!wordTextField.getText().isEmpty()) {
				String newWord = wordTextField.getText().toLowerCase();
				Integer newWeight = (Integer) weightSpinner.getValue();

				keywordMap.put(newWord, newWeight);

				// Update the JList
				int indexKeywordElement = -1;
				for (KeywordElement keyword : keywordListModel.getElements()) {
					if (newWord.equals(keyword.word)) {
						indexKeywordElement = keywordListModel.getElements().indexOf(keyword);
					}
				}
				if (indexKeywordElement != -1) {
					keywordListModel.getElementAt(indexKeywordElement).weight = newWeight;
				} else {
					KeywordElement newKeywordElement = new KeywordElement();
					newKeywordElement.word = newWord;
					newKeywordElement.weight = newWeight;
					keywordListModel.addElement(newKeywordElement);
				}
				keywordJList.updateUI();
			}
		}
	}

	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (keywordJList.getSelectedValue() instanceof KeywordElement) {
				KeywordElement keywordSelected = keywordJList.getSelectedValue();
				keywordMap.remove(keywordSelected.word);
				keywordListModel.removeElement(keywordSelected);
			}
		}
	}

}