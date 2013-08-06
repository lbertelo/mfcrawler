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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.export.ExportResults.EFormatExport;
import org.mfcrawler.model.export.ExportResults.EScopeExport;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Panel for export
 * 
 * @author lbertelo
 */
public class ExportPanel extends DefaultPanel {

	private JTextField minScore;
	private JRadioButton pageScopeButton;
	private JRadioButton siteScopeButton;
	private JRadioButton gexfFormatButton;
	private JRadioButton csvDataFormatButton;
	private JRadioButton csvLinksFormatButton;
	private JFileChooser fileChooser;

	public ExportPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("export.title"), I18nUtil.getMessage("export.description"));
	}

	@Override
	protected JComponent buildContent() {
		fileChooser = new JFileChooser();

		JPanel tempPanel;
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JPanel scopePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		scopePanel.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("export.scope")));
		tempPanel = new JPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
		ButtonGroup scopeGroup = new ButtonGroup();
		siteScopeButton = new JRadioButton(I18nUtil.getMessage("export.scope.site"));
		scopeGroup.add(siteScopeButton);
		tempPanel.add(siteScopeButton);
		pageScopeButton = new JRadioButton(I18nUtil.getMessage("export.scope.page"), true);
		scopeGroup.add(pageScopeButton);
		tempPanel.add(pageScopeButton);
		scopePanel.add(tempPanel);

		panel.add(scopePanel);
		JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		formatPanel.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("export.format")));
		tempPanel = new JPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
		ButtonGroup formatGroup = new ButtonGroup();
		gexfFormatButton = new JRadioButton(I18nUtil.getMessage("export.format.gexf"), true);
		formatGroup.add(gexfFormatButton);
		tempPanel.add(gexfFormatButton);
		csvLinksFormatButton = new JRadioButton(I18nUtil.getMessage("export.format.csv.links"));
		formatGroup.add(csvLinksFormatButton);
		tempPanel.add(csvLinksFormatButton);
		csvDataFormatButton = new JRadioButton(I18nUtil.getMessage("export.format.csv.data"));
		formatGroup.add(csvDataFormatButton);
		tempPanel.add(csvDataFormatButton);
		formatPanel.add(tempPanel);

		panel.add(formatPanel);
		JPanel paramsPanel = new JPanel();
		paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.PAGE_AXIS));
		paramsPanel.setBorder(BorderFactory.createTitledBorder(I18nUtil.getMessage("export.export")));
		tempPanel = new JPanel(new FlowLayout());
		JLabel minScoreLabel = new JLabel(I18nUtil.getMessage("export.minimumScore"));
		minScore = new JTextField("0", 5);
		tempPanel.add(minScoreLabel);
		tempPanel.add(minScore);
		paramsPanel.add(tempPanel);
		
		tempPanel = new JPanel(new FlowLayout());
		JButton exportButton = new JButton(I18nUtil.getMessage("export.export"));
		exportButton.addActionListener(new ExportAction());
		tempPanel.add(exportButton);
		paramsPanel.add(tempPanel, BorderLayout.CENTER);

		panel.add(paramsPanel);

		return panel;
	}

	private EScopeExport getScopeSelected() {
		EScopeExport scope;
		if (siteScopeButton.isSelected()) {
			scope = EScopeExport.SITE;
		} else {
			scope = EScopeExport.PAGE;
		}
		return scope;
	}
	
	private EFormatExport getFileFormatSelected() {
		EFormatExport format;
		if (gexfFormatButton.isSelected()) {
			format = EFormatExport.GEXF;
		} else if (csvLinksFormatButton.isSelected()) {
			format = EFormatExport.CSV_LINKS;
		} else {
			format = EFormatExport.CSV_DATA;
		}
		return format;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Do nothing
	}

	// File Filters

	private class GexfFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(I18nUtil.getMessage("export.fileFilters.gexf.extension"));
		}

		@Override
		public String getDescription() {
			return I18nUtil.getMessage("export.fileFilters.gexf.description");
		}
	}

	private class CSVFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(I18nUtil.getMessage("export.fileFilters.csv.extension"));
		}

		@Override
		public String getDescription() {
			return I18nUtil.getMessage("export.fileFilters.csv.description");
		}
	}

	// Listener Class

	private class ExportAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!minScore.getText().isEmpty()) {
				Double minScoreValue = ConversionUtils.toDouble(minScore.getText());
				EScopeExport scope = getScopeSelected();
				EFormatExport format = getFileFormatSelected();
				
				if (format == EFormatExport.GEXF) {
					fileChooser.setFileFilter(new GexfFileFilter());
				} else {
					fileChooser.setFileFilter(new CSVFileFilter());
				}
				
				int returnVal = fileChooser.showSaveDialog(getView().getFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					getModel().exportResult(file, scope, format, minScoreValue);
				}
			}
		}

	}

}
