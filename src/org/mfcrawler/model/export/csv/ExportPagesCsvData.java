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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mfcrawler.model.dao.export.ExportPageDAO;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.export.gexf.ExportSitesGexf;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.util.ConversionUtils;

/**
 * Exports data of pages in a CSV file
 * 
 * @author lbertelo
 */
public final class ExportPagesCsvData implements ICsvParams {

	/**
	 * Header of the CSV
	 */
	private static final String CSV_HEADER = "link, crawl time, inner deep, outer deep, score\n";

	/**
	 * Private constructor
	 */
	private ExportPagesCsvData() {
	}

	/**
	 * Exports data of pages with a minimum score in a CSV file
	 * @param file the file used to export
	 * @param minScore minimum score for a page
	 */
	public static void export(File file, Double minScore) {
		try {
			FileWriter csvFileWriter = new FileWriter(file, false);
			csvFileWriter.write(CSV_HEADER);

			ExportPageDAO exportPageDao = new ExportPageDAO();
			PageDbIterator pageIterator = exportPageDao.getPageListToExport(minScore);

			while (pageIterator.hasNext()) {
				Page page = pageIterator.next();
				csvFileWriter.write(StringEscapeUtils.escapeCsv(page.getLink().getUrl()));
				csvFileWriter.write(CSV_COLUMN_SEPARATOR);
				csvFileWriter.write(StringEscapeUtils.escapeCsv(ConversionUtils.toString(page.getCrawlTime())));
				csvFileWriter.write(CSV_COLUMN_SEPARATOR);
				csvFileWriter.write(ConversionUtils.toString(page.getInnerDeep()));
				csvFileWriter.write(CSV_COLUMN_SEPARATOR);
				csvFileWriter.write(ConversionUtils.toString(page.getOuterDeep()));
				csvFileWriter.write(CSV_COLUMN_SEPARATOR);
				csvFileWriter.write(ConversionUtils.toString(page.getScore()));
				csvFileWriter.write(CSV_LINE_SEPARATOR);
			}

			csvFileWriter.flush();
			csvFileWriter.close();
		} catch (IOException ie) {
			Logger.getLogger(ExportSitesGexf.class.getName()).log(Level.SEVERE, "Error to write CSV data", ie);
		}
	}

}
