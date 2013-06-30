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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.model.pojo.ApplicationConfig;
import org.mfcrawler.model.pojo.IAppConfigParams;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;
import org.mfcrawler.view.ApplicationView;

/**
 * Dialog for application configuration (expect proxy)
 * 
 * @author lbertelo
 */
public class GeneralConfigDialog extends DefaultDialog implements IAppConfigParams {

	private static final int DIALOG_WIDTH = 350;
	private static final int DIALOG_HEIGHT = 250;
	private static final int USER_AGENT_COLUMNS = 15;
	private static final int FORBIDDEN_FILE_EXT_ROWS = 3;
	private static final int FORBIDDEN_FILE_EXT_COLUMNS = 30;

	private JDialog dialog;
	private JTextField userAgentText;
	private JSpinner pageRequestTimeoutSpinner;
	private JSpinner robotsRequestTimeoutSpinner;
	private JTextArea forbiddenFileExtensionsTextArea;

	public GeneralConfigDialog(ApplicationView view, ApplicationModel model) {
		super(view, model);
	}

	@Override
	public void display() {
		ApplicationConfig config = ApplicationModel.getConfig();

		userAgentText.setText(config.getUserAgent());
		pageRequestTimeoutSpinner.setValue(config.getPageRequestTimeout());
		robotsRequestTimeoutSpinner.setValue(config.getRobotsRequestTimeout());
		forbiddenFileExtensionsTextArea.setText(config.getForbiddenFileExtensions());

		super.display();
	}

	@Override
	protected JDialog buildDialog() {
		dialog = new JDialog(getView().getFrame(), I18nUtil.getMessage("dialog.applicationConfig"), true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		// TODO rendre le user agent modifiable, faire attention aux effets de
		// bord
		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel userAgentLabel = new JLabel(I18nUtil.getMessage("dialog.applicationConfig.userAgent"));
		tempPanel.add(userAgentLabel);
		userAgentText = new JTextField(USER_AGENT_COLUMNS);
		userAgentText.setEnabled(false);
		tempPanel.add(userAgentText);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel pageRequestTimeoutLabel = new JLabel(I18nUtil.getMessage("dialog.applicationConfig.pageRequestTimeout"));
		tempPanel.add(pageRequestTimeoutLabel);
		pageRequestTimeoutSpinner = new JSpinner(new SpinnerNumberModel(PAGE_REQUEST_TIMEOUT_DEFAULT.intValue(),
				REQUEST_TIMEOUT_MIN, REQUEST_TIMEOUT_MAX, REQUEST_TIMEOUT_SPINNER_STEP));
		tempPanel.add(pageRequestTimeoutSpinner);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel robotsRequestTimeoutLabel = new JLabel(
				I18nUtil.getMessage("dialog.applicationConfig.robotsRequestTimeout"));
		tempPanel.add(robotsRequestTimeoutLabel);
		robotsRequestTimeoutSpinner = new JSpinner(new SpinnerNumberModel(ROBOTS_REQUEST_TIMEOUT_DEFAULT.intValue(),
				REQUEST_TIMEOUT_MIN, REQUEST_TIMEOUT_MAX, REQUEST_TIMEOUT_SPINNER_STEP));
		tempPanel.add(robotsRequestTimeoutSpinner);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel forbiddenFileExtensionsLabel = new JLabel(
				I18nUtil.getMessage("dialog.applicationConfig.forbiddenFileExtensions"));
		tempPanel.add(forbiddenFileExtensionsLabel);
		forbiddenFileExtensionsTextArea = new JTextArea(FORBIDDEN_FILE_EXT_ROWS, FORBIDDEN_FILE_EXT_COLUMNS);
		forbiddenFileExtensionsTextArea.setLineWrap(true);
		tempPanel.add(forbiddenFileExtensionsTextArea);
		dialog.getContentPane().add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton applyButton = new JButton(I18nUtil.getMessage("general.ok"));
		applyButton.addActionListener(new ApplyAction());
		tempPanel.add(applyButton);
		JButton cancelButton = new JButton(I18nUtil.getMessage("general.cancel"));
		cancelButton.addActionListener(new CancelAction());
		tempPanel.add(cancelButton);
		dialog.getContentPane().add(tempPanel);

		return dialog;
	}

	// Listener Classes

	private class ApplyAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ApplicationConfig config = ApplicationModel.getConfig();

			config.setUserAgent(userAgentText.getText());
			config.setPageRequestTimeout(ConversionUtils.toInteger(pageRequestTimeoutSpinner.getValue()));
			config.setRobotsRequestTimeout(ConversionUtils.toInteger(robotsRequestTimeoutSpinner.getValue()));
			config.setForbiddenFileExtensions(forbiddenFileExtensionsTextArea.getText());

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
