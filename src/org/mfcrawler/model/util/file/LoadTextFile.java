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

package org.mfcrawler.model.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Allows to load text file
 * 
 * @author lbertelo
 */
public class LoadTextFile {

	/**
	 * Load a file which contains links
	 * @param linkFile the link file
	 * @return the list of links
	 */
	public static List<Link> loadLinkList(File linkFile) {
		List<Link> linkList = new ArrayList<Link>();
		Link tmpLink;
		if (linkFile.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(linkFile));
				while (bufferedReader.ready()) {
					String line = bufferedReader.readLine().trim();
					tmpLink = Link.parseUrl(line);
					if (tmpLink != null) {
						linkList.add(tmpLink);
					}
				}
				bufferedReader.close();
			} catch (Exception e) {
				Logger.getLogger(LoadTextFile.class.getName()).log(Level.WARNING, "Error to read link list file", e);
			}
		}
		return linkList;
	}

}
