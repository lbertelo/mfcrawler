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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mfcrawler.model.dao.export.ExportPageDAO;
import org.mfcrawler.model.dao.export.ExportSiteDAO;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;
import org.mfcrawler.model.pojo.site.link.Domain;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.ConversionUtils;
import org.mfcrawler.model.util.I18nUtil;

/**
 * Exports sites in a GEXF file sites are nodes and links are edges
 * 
 * @author lbertelo
 */
public final class ExportSitesGexf {

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
	private ExportSitesGexf() {
	}

	// Main

	/**
	 * Exports sites with a minimum score in a GEXF file
	 * @param file the file used to export
	 * @param minTotalScore minimum total score for a site
	 */
	public static void export(File file, Double minTotalScore) {
		List<Domain> domainNodesList = null;

		try {
			FileWriter gexfFileWriter = new FileWriter(file, false);

			writeGexfHeader(gexfFileWriter);

			domainNodesList = generateSitesNodes(gexfFileWriter, minTotalScore);

			generatePagesEdges(gexfFileWriter, domainNodesList);

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
		fileWriter.write("<attribute id=\"totalScore\" title=\"totalScore\" type=\"double\" /> ");
		fileWriter.write("<attribute id=\"averageScore\" title=\"averageScore\" type=\"double\" /> ");
		fileWriter.write("<attribute id=\"crawledPagesNumber\" title=\"crawledPagesNumber\" type=\"integer\" /> </attributes> \"> \n\n");
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
	 * Gets list of sites (with a minimum score) to export and write nodes
	 * @param fileWriter the file writer
	 * @param minScore the minimum total score
	 * @return the list of domains collected
	 * @exception
	 */
	private static List<Domain> generateSitesNodes(FileWriter fileWriter, Double minTotalScore) throws IOException {
		List<Domain> domainNodesList = new ArrayList<Domain>();
		ExportSiteDAO exportSiteDao = new ExportSiteDAO();

		List<Site> siteList = exportSiteDao.getSiteListToExport(minTotalScore);

		fileWriter.write("\n<nodes>\n");
		for (Site site : siteList) {
			domainNodesList.add(site.getDomain());
			writeGexfNode(fileWriter, site);
		}
		fileWriter.write("\n</nodes>\n");

		return domainNodesList;
	}

	/**
	 * Writes a gexf node from a site
	 * @param fileWriter the file write
	 * @param site the site (the node)
	 * @exception
	 */
	private static void writeGexfNode(FileWriter fileWriter, Site site) throws IOException {
		String domainName = StringEscapeUtils.escapeXml(site.getDomain().getName());
		fileWriter.write("<node id=\"");
		fileWriter.write(domainName);
		fileWriter.write("\" label=\"");
		fileWriter.write(domainName);
		fileWriter.write("\" start=\"");
		fileWriter.write(ConversionUtils.toFormattedDate(site.getCrawlTime(), DATETIME_FORMAT));

		fileWriter.write("\"> <attvalues> <attvalue for=\"totalScore\" value=\"");
		fileWriter.write(ConversionUtils.toString(site.getTotalScore()));
		fileWriter.write("\"/> <attvalues> <attvalue for=\"averageScore\" value=\"");
		fileWriter.write(ConversionUtils.toString(site.getTotalScore() / site.getCrawledPagesNumber()));
		fileWriter.write("\"/> <attvalue for=\"crawledPagesNumber\" value=\"");
		fileWriter.write(ConversionUtils.toString(site.getCrawledPagesNumber()));
		fileWriter.write("\"/> </attvalues> </node> \n");
	}

	// Edges

	/**
	 * Gets list of pages and links to export and write edges
	 * @param fileWriter the file writer
	 * @param domainNodesList the list of domains allowed (nodes existing)
	 * @exception
	 */
	private static void generatePagesEdges(FileWriter fileWriter, List<Domain> domainNodesList) throws IOException {
		ExportPageDAO exportPageDao = new ExportPageDAO();

		fileWriter.write("\n<edges>\n");

		if (!domainNodesList.isEmpty()) {
			int edgeId = 0;
			
			PageDbIterator pageIterator = exportPageDao.getPageListToExport(domainNodesList);
			while (pageIterator.hasNext()) {
				Page page = pageIterator.next();
				List<Link> outgoingExternLinks = exportPageDao.getOutgoingExternLinks(page, domainNodesList);
				for (Link externLink : outgoingExternLinks) {
					writeGexfEdge(fileWriter, edgeId++, page, externLink);
				}
			}
		}

		fileWriter.write("\n</edges>\n");
	}

	/**
	 * Writes a gexf edge from a domain's page and a domain's link
	 * @param fileWriter the file write
	 * @param edgeId the edge id
	 * @param page the page, the source of the edge
	 * @param externLink the externLink, the target of the edge
	 * @exception
	 */
	private static void writeGexfEdge(FileWriter fileWriter, int edgeId, Page page, Link externLink) throws IOException {
		fileWriter.write("<edge id=\"");
		fileWriter.write(ConversionUtils.toString(edgeId));
		fileWriter.write("\" source=\"");
		fileWriter.write(StringEscapeUtils.escapeXml(page.getLink().getDomain().getName()));
		fileWriter.write("\" target=\"");
		fileWriter.write(StringEscapeUtils.escapeXml(externLink.getDomain().getName()));
		fileWriter.write("\" type=\"directed\" /> \n");
	}

}
