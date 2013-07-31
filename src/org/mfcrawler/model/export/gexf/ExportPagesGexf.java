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

package org.mfcrawler.model.export.gexf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mfcrawler.model.dao.export.ExportPageDAO;
import org.mfcrawler.model.dao.iterator.LinkDbIterator;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;

/**
 * Exports pages in a GEXF file pages are nodes and links are edges
 * 
 * @author lbertelo
 */
public final class ExportPagesGexf {

	/**
	 * Date format for the GEXF header
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Datetime format for nodes
	 */
	private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	/**
	 * Private constructor
	 */
	private ExportPagesGexf() {
	}

	// Main

	/**
	 * Exports pages with a minimum score in a GEXF file
	 * @param file the file used to export
	 * @param minScore minimum score for a page
	 */
	public static void export(File file, Double minScore) {
		try {
			FileWriter gexfFileWriter = new FileWriter(file, false);

			writeGexfHeader(gexfFileWriter);

			generatePagesNodes(gexfFileWriter, minScore);

			generateLinksEdges(gexfFileWriter, minScore);

			writeGexfFooter(gexfFileWriter);

			gexfFileWriter.flush();
			gexfFileWriter.close();
		} catch (IOException ie) {
			Logger.getLogger(ExportSitesGexf.class.getName()).log(Level.SEVERE, "Error to write gexf", ie);
		}
	}

	/**
	 * Writes the GEXF header
	 * @param fileWriter the file write
	 * @exception
	 */
	private static void writeGexfHeader(FileWriter fileWriter) throws IOException {
		fileWriter.write("<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" ");
		fileWriter.write("xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		fileWriter.write("xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\"> \n");
		fileWriter.write("<meta lastmodifieddate=\"");
		fileWriter.write(ConversionUtils.toFormattedDate(new Date(), DATE_FORMAT));
		fileWriter.write("\"> \n");
		fileWriter.write("<creator> ");
		fileWriter.write(I18nUtil.getMessage("export.creator"));
		fileWriter.write(" </creator> \n <description> ");
		fileWriter.write(I18nUtil.getMessage("export.description"));
		fileWriter.write(" </description> \n");
		fileWriter.write("</meta> \n <graph defaultedgetype=\"directed\" idtype=\"string\" mode=\"dynamic\" timeformat=\"dateTime\"> ");
		fileWriter.write("<attributes class=\"node\" mode=\"static\"> ");
		fileWriter.write("<attribute id=\"score\" title=\"score\" type=\"double\" /> ");
		fileWriter.write("<attribute id=\"innerDeep\" title=\"innerDeep\" type=\"integer\" /> ");
		fileWriter.write("<attribute id=\"outerDeep\" title=\"outerDeep\" type=\"integer\" /> </attributes> \"> \n\n");
	}

	/**
	 * Writes the GEXF footer
	 * @param fileWriter the file write
	 * @exception
	 */
	private static void writeGexfFooter(FileWriter fileWriter) throws IOException {
		fileWriter.write("\n </graph> \n </gexf>");
	}

	// Nodes

	/**
	 * Gets list of pages (with a minimum score) to export and write nodes
	 * @param fileWriter the file writer
	 * @param minScore the minimum score
	 * @exception
	 */
	private static void generatePagesNodes(FileWriter fileWriter, Double minScore) throws IOException {
		ExportPageDAO exportPageDao = new ExportPageDAO();
		PageDbIterator pageIterator = exportPageDao.getPageListToExport(minScore);

		fileWriter.write("\n<nodes>\n");
		while (pageIterator.hasNext()) {
			Page page = pageIterator.next();
			writeGexfNode(fileWriter, page);
		}
		fileWriter.write("\n</nodes>\n");
	}

	/**
	 * Writes a gexf node from a page
	 * @param fileWriter the file write
	 * @param site the page (the node)
	 * @exception
	 */
	private static void writeGexfNode(FileWriter fileWriter, Page page) throws IOException {
		String linkName = StringEscapeUtils.escapeXml(page.getLink().getUrl());
		fileWriter.write("<node id=\"");
		fileWriter.write(linkName);
		fileWriter.write("\" label=\"");
		fileWriter.write(linkName);
		fileWriter.write("\" start=\"");
		fileWriter.write(ConversionUtils.toFormattedDate(page.getCrawlTime(), DATETIME_FORMAT));

		fileWriter.write("\"> <attvalues> <attvalue for=\"score\" value=\"");
		fileWriter.write(ConversionUtils.toString(page.getScore()));
		fileWriter.write("\"/> <attvalue for=\"innerDeep\" value=\"");
		fileWriter.write(ConversionUtils.toString(page.getInnerDeep()));
		fileWriter.write("\"/> <attvalue for=\"outerDeep\" value=\"");
		fileWriter.write(ConversionUtils.toString(page.getOuterDeep()));
		fileWriter.write("\"/> </attvalues> </node> \n");
	}

	// Edges

	/**
	 * Gets list of pages and links to export and write edges
	 * @param fileWriter the file writer
	 * @param minScore the minimum score
	 * @exception
	 */
	private static void generateLinksEdges(FileWriter fileWriter, Double minScore) throws IOException {
		fileWriter.write("\n<edges>\n");
		int edgeId = 0;
		
		ExportPageDAO exportPageDao = new ExportPageDAO();
		LinkDbIterator linkIterator = exportPageDao.getSourceLinkList(minScore);

		while (linkIterator.hasNext()) {
			Link sourceLink = linkIterator.next();
			List<Link> targetLinkList = exportPageDao.getTargetLinkList(sourceLink, minScore);
			for (Link targetLink : targetLinkList) {
				writeGexfEdge(fileWriter, edgeId++, sourceLink, targetLink);
			}
		}

		fileWriter.write("\n</edges>\n");
	}

	/**
	 * Writes a gexf edge from a source link (correct page) and a target link
	 * @param fileWriter the file write
	 * @param edgeId the edge id
	 * @param sourceLink the source link, the source of the edge
	 * @param targetLink the outgoing link, the target of the edge
	 * @exception
	 */
	private static void writeGexfEdge(FileWriter fileWriter, int edgeId, Link sourceLink, Link targetLink)
			throws IOException {
		fileWriter.write("<edge id=\"");
		fileWriter.write(ConversionUtils.toString(edgeId));
		fileWriter.write("\" source=\"");
		fileWriter.write(StringEscapeUtils.escapeXml(sourceLink.getUrl()));
		fileWriter.write("\" target=\"");
		fileWriter.write(StringEscapeUtils.escapeXml(targetLink.getUrl()));
		fileWriter.write("\" type=\"directed\" /> \n");
	}

}
