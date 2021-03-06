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

package org.mfcrawler;

import org.mfcrawler.model.ApplicationModel;
import org.mfcrawler.view.ApplicationView;

/**
 * The main class which launches the software
 * @author lbertelo
 */
public class Main {

	/**
	 * The main method
	 * @param args not used
	 */
	public static void main(String[] args) {
		// Model
		ApplicationModel model = new ApplicationModel();

		// View
		ApplicationView view = new ApplicationView(model);
		view.display();

		// Init (load the last project)
		model.initModel();
	}

}
