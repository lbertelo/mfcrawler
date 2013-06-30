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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.export.ExportSitesGexfFile;
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
	private JFileChooser fileChooser;

	public ExportPanel(ApplicationView view, ApplicationModel model) {
		super(view, model, I18nUtil.getMessage("export.title"), I18nUtil.getMessage("export.description"));
	}

	@Override
	protected JComponent buildContent() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".gexf");
			}

			@Override
			public String getDescription() {
				return "Graph Exchange XML Format (*.gexf)";
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("À changer ici"));

		JPanel tempPanel = new JPanel(new FlowLayout());
		JLabel minScoreLabel = new JLabel(I18nUtil.getMessage("export.minimumScore"));
		minScore = new JTextField("0", 5);
		tempPanel.add(minScoreLabel);
		tempPanel.add(minScore);
		panel.add(tempPanel, BorderLayout.PAGE_START);

		tempPanel = new JPanel(new FlowLayout());
		JButton exportButton = new JButton(I18nUtil.getMessage("export.exportGexf"));
		exportButton.addActionListener(new ExportGEXF());
		tempPanel.add(exportButton);
		panel.add(tempPanel, BorderLayout.CENTER);

		return panel;
	}

	// Listener Class

	private class ExportGEXF implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Integer minScoreValue;
			if (!minScore.getText().isEmpty()) {
				minScoreValue = ConversionUtils.toInteger(minScore.getText());
				int returnVal = fileChooser.showSaveDialog(getView().getFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.getName().endsWith(".gexf")) {
						file = new File(file.getAbsolutePath() + ".gexf");
					}
					ExportSitesGexfFile.exportSitesGexf(file, minScoreValue);
				}
			}
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Do nothing
	}

}
