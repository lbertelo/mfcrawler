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
package org.mfcrawler.model.export.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mfcrawler.model.dao.export.ExportPageDAO;
import org.mfcrawler.model.dao.iterator.LinkDbIterator;
import org.mfcrawler.model.export.gexf.ExportSitesGexf;
import org.mfcrawler.model.pojo.site.link.Link;

/**
 * Exports links of pages in a CSV file
 * 
 * @author lbertelo
 */
public final class ExportPagesCsvLinks {

	/**
	 * Header of the CSV
	 */
	private static final String CSV_HEADER = "a source page, a target link\n";

	/**
	 * Column's separator
	 */
	private static final String CSV_COLUMN_SEPARATOR = ",";

	/**
	 * Line's separator
	 */
	private static final String CSV_LINE_SEPARATOR = "\n";

	/**
	 * Private constructor
	 */
	private ExportPagesCsvLinks() {
	}

	/**
	 * Exports links of pages with a minimum score in a CSV file
	 * @param file the file used to export
	 * @param minScore minimum score for a page
	 */
	public static void export(File file, Double minScore) {
		try {
			FileWriter csvFileWriter = new FileWriter(file, false);
			csvFileWriter.write(CSV_HEADER);

			ExportPageDAO exportPageDao = new ExportPageDAO();
			LinkDbIterator linkIterator = exportPageDao.getSourceLinkList(minScore);
			
			while (linkIterator.hasNext()) {
				Link sourceLink = linkIterator.next();
				List<Link> targetLinkList = exportPageDao.getTargetLinkList(sourceLink, minScore);
				for (Link targetLink : targetLinkList) {
					csvFileWriter.write(StringEscapeUtils.escapeCsv(sourceLink.getUrl()));
					csvFileWriter.write(CSV_COLUMN_SEPARATOR);
					csvFileWriter.write(StringEscapeUtils.escapeCsv(targetLink.getUrl()));
					csvFileWriter.write(CSV_LINE_SEPARATOR);
				}
			}

			csvFileWriter.flush();
			csvFileWriter.close();
		} catch (IOException ie) {
			Logger.getLogger(ExportSitesGexf.class.getName()).log(Level.SEVERE, "Error to write CSV links", ie);
		}
	}

}
