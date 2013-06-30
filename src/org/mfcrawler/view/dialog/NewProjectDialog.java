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

package org.mfcrawler.view.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Dialog for creating new project
 * 
 * @author lbertelo
 */
public class NewProjectDialog extends DefaultDialog {

	private static final int DIALOG_WIDTH = 300;
	private static final int DIALOG_HEIGHT = 150;
	private static final int NEW_PROJECT_COLUMNS = 20;

	private JDialog dialog;
	private JTextField projectNameText;

	public NewProjectDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);
	}

	@Override
	public void display() {
		projectNameText.setText("");
		super.display();
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.newProject"), true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel projectNameLabel = new JLabel(I18nUtil.getMessage("dialog.newProject.projectName"));
		tempPanel.add(projectNameLabel);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		projectNameText = new JTextField(NEW_PROJECT_COLUMNS);
		tempPanel.add(projectNameText);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton createButton = new JButton(I18nUtil.getMessage("general.create"));
		createButton.addActionListener(new CreateAction());
		tempPanel.add(createButton);
		JButton cancelButton = new JButton(I18nUtil.getMessage("general.cancel"));
		cancelButton.addActionListener(new CancelAction());
		tempPanel.add(cancelButton);
		dialog.getContentPane().add(tempPanel);

		return dialog;
	}

	// Listener Classes

	private class CreateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String projectName = projectNameText.getText().trim();
			if (!projectName.isEmpty()) {
				getModel().loadCrawlProject(projectName);
			}
			hide();
		}
	}

	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			hide();
		}
	}

}
