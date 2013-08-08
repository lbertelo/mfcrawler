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
import org.mfcrawler.model.dao.export.ExportSiteDAO;
import org.mfcrawler.model.export.gexf.ExportSitesGexf;
import org.mfcrawler.model.pojo.site.link.Domain;

/**
 * Exports links of sites (only domain's name) in a CSV file
 * 
 * @author lbertelo
 */
public final class ExportSitesCsvLinks implements ICsvParams {

	/**
	 * Header of the CSV
	 */
	private static final String CSV_HEADER = "source site, target site\n";

	/**
	 * Private contructor
	 */
	private ExportSitesCsvLinks() {
	}

	/**
	 * Exports links of sites with a minimum score in a CSV file
	 * @param file the file used to export
	 * @param minTotalScore minimum total score for a site
	 */
	public static void export(File file, Double minTotalScore) {
		try {
			FileWriter csvFileWriter = new FileWriter(file, false);
			csvFileWriter.write(CSV_HEADER);

			ExportSiteDAO exportSiteDao = new ExportSiteDAO();
			List<Domain> domainList = exportSiteDao.getDomainListToExport(minTotalScore);
			
			for (Domain sourceDomain : domainList) {
				List<Domain> targetDomainList = exportSiteDao.getTargetDomainList(sourceDomain, domainList);
				for (Domain targetDomain : targetDomainList) {
					csvFileWriter.write(StringEscapeUtils.escapeCsv(sourceDomain.getName()));
					csvFileWriter.write(CSV_COLUMN_SEPARATOR);
					csvFileWriter.write(StringEscapeUtils.escapeCsv(targetDomain.getName()));
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
