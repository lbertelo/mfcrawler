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

package org.mfcrawler.model.export;

import java.io.File;

import org.mfcrawler.model.export.csv.ExportPagesCsvData;
import org.mfcrawler.model.export.csv.ExportPagesCsvLinks;
import org.mfcrawler.model.export.csv.ExportSitesCsvData;
import org.mfcrawler.model.export.csv.ExportSitesCsvLinks;
import org.mfcrawler.model.export.gexf.ExportPagesGexf;
import org.mfcrawler.model.export.gexf.ExportSitesGexf;

/**
 * Main Class to export results in files
 * 
 * @author lbertelo
 */
public final class ExportResults {

	/**
	 * GEXF extension
	 */
	private static final String GEXF_EXTENSION = ".gexf";

	/**
	 * CSV extension
	 */
	private static final String CSV_EXTENSION = ".csv";

	/**
	 * Scope to export
	 */
	public enum EScopeExport {
		PAGE, SITE
	}

	/**
	 * File's format to export
	 */
	public enum EFormatExport {
		GEXF, CSV_LINKS, CSV_DATA
	}

	/**
	 * Private constructor
	 */
	private ExportResults() {
	}

	/**
	 * Export the result (crawled page) to different format and scope
	 * @param file the file containing the export result
	 * @param scope the scope of the export
	 * @param format the format of the export
	 * @param minScoreValue the minimum score value to export
	 */
	public static void export(File file, EScopeExport scope, EFormatExport format, Double minScoreValue) {
		File correctFile = getCorrectFile(file, format);

		if (format == EFormatExport.GEXF) {
			if (scope == EScopeExport.SITE) {
				ExportSitesGexf.export(correctFile, minScoreValue);
			} else if (scope == EScopeExport.PAGE) {
				ExportPagesGexf.export(correctFile, minScoreValue);
			}
		} else if (format == EFormatExport.CSV_LINKS) {
			if (scope == EScopeExport.SITE) {
				ExportSitesCsvLinks.export(correctFile, minScoreValue);
			} else if (scope == EScopeExport.PAGE) {
				ExportPagesCsvLinks.export(correctFile, minScoreValue);
			}
		} else if (format == EFormatExport.CSV_DATA) {
			if (scope == EScopeExport.SITE) {
				ExportSitesCsvData.export(correctFile, minScoreValue);
			} else if (scope == EScopeExport.PAGE) {
				ExportPagesCsvData.export(correctFile, minScoreValue);
			}
		}
	}

	/**
	 * Gets the file with the correct extension
	 * @param file the file
	 * @param format the format of the file
	 * @return the file with the correct extension
	 */
	private static File getCorrectFile(File file, EFormatExport format) {
		File correctFile = file;

		String extension;
		if (format == EFormatExport.GEXF) {
			extension = GEXF_EXTENSION;
		} else {
			extension = CSV_EXTENSION;
		}

		if (!file.getName().endsWith(extension)) {
			correctFile = new File(file.getAbsolutePath() + extension);
		}

		return correctFile;
	}

}
