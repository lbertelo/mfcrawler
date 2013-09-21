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

package org.mfcrawler.model.process.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.Site;

/**
 * Analyzes words of a page, a site
 * 
 * @author lbertelo
 */
public class WordAnalysisUtil {

	/**
	 * Data analysis for all the crawled sites
	 */
	public class GlobalData {
		public String word;
		public Integer pageOccurrence;
		public Double wordOccurrence;
		public Double tfIdf, weightedTfIdf;
	}

	/**
	 * Analyzes the content of a page
	 * @param page the analyzed page
	 * @return the result of the analysis
	 */
	public static List<BasicAnalysis> analyze(Page page) {
		List<BasicAnalysis> analysisResult = new ArrayList<BasicAnalysis>();
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();

		if (page.getContent() != null) {
			KeywordManager.countOccurrences(wordsOccurrences, page.getContent(), keywordMap.keySet());
		}
		initKeywordsOccurences(wordsOccurrences, keywordMap);

		for (String word : wordsOccurrences.keySet()) {
			analysisResult.add(new PageAnalysis(word, wordsOccurrences.get(word), keywordMap.get(word)));
		}

		return analysisResult;
	}

	/**
	 * Analyzes the page's content of a site
	 * @param site the analyzed site
	 * @return the result of the analysis
	 */
	public static List<BasicAnalysis> analyze(Site site) {
		List<BasicAnalysis> analysisResult = new ArrayList<BasicAnalysis>();
		Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();

		PageDAO pageDao = new PageDAO();
		PageDbIterator pageDbIterator = pageDao.getPagesWithContent(site.getDomain());
		while (pageDbIterator.hasNext()) {
			Page page = pageDbIterator.next();
			if (page.getContent() != null) {
				KeywordManager.countOccurrences(wordsOccurrences, page.getContent(), keywordMap.keySet());
			}
		}
		initKeywordsOccurences(wordsOccurrences, keywordMap);

		for (String word : wordsOccurrences.keySet()) {
			analysisResult.add(new BasicAnalysis(word, wordsOccurrences.get(word), keywordMap.get(word)));
		}

		return analysisResult;
	}

	/**
	 * Initializes keywords in the wordsOccurences Map
	 * @param wordsOccurrences
	 * @param keywordMap
	 */
	private static void initKeywordsOccurences(Map<String, Integer> wordsOccurrences, Map<String, Integer> keywordMap) {
		for (String keyword : keywordMap.keySet()) {
			if (wordsOccurrences.get(keyword) == null) {
				wordsOccurrences.put(keyword, 0);
			}
		}
	}

}
