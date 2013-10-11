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
 * Analyzes words of a page, a site, or all the crawled pages
 * 
 * @author lbertelo
 */
public class WordAnalysisUtil {

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

	/**
	 * Analyzes the content of all the crawled pages with content
	 * @return the result of the analysis
	 */
	public static List<GlobalAnalysis> analyze() {
		Map<String, Integer> keywordMap = KeywordManager.getKeywordMap();

		double nbDocsTotal = 0;
		double scoresTotal = 0;
		Map<String, GlobalSum> globalSumMap = new HashMap<String, GlobalSum>();

		PageDAO pageDao = new PageDAO();
		PageDbIterator pageDbIterator = pageDao.getPagesWithContent();
		// Explore documents
		while (pageDbIterator.hasNext()) {
			Page page = pageDbIterator.next();
			if (page.getContent() != null) {
				nbDocsTotal += 1.0;
				scoresTotal += page.getScore();

				Map<String, Integer> wordsOccurrences = new HashMap<String, Integer>();
				KeywordManager.countOccurrences(wordsOccurrences, page.getContent(), keywordMap.keySet());

				// Explore words of the document
				for (String word : wordsOccurrences.keySet()) {
					Integer occurrence = wordsOccurrences.get(word);
					Double frequency = Math.log(occurrence);

					GlobalSum globalSum = globalSumMap.get(word);
					if (globalSum == null) {
						globalSumMap.put(word, new GlobalSum(frequency, page.getScore()));
					} else {
						globalSum.increment(frequency, page.getScore());
					}
				}
			}
		}

		// Final counting
		List<GlobalAnalysis> analysisResult = new ArrayList<GlobalAnalysis>();
		for (String word : globalSumMap.keySet()) {
			GlobalSum globalSum = globalSumMap.get(word);
			Double wordFrequency = globalSum.getWordsFreqSum() / nbDocsTotal;
			Double docFrequency = globalSum.getNbDocsSum() / nbDocsTotal;
			Double tfIdf = wordFrequency * Math.log(1.0 / docFrequency);
			Double weightedTfIdf = tfIdf * (globalSum.getScoreDocsSum() / scoresTotal);
			analysisResult.add(new GlobalAnalysis(word, wordFrequency, docFrequency, tfIdf, weightedTfIdf));
		}

		return analysisResult;
	}

	/**
	 * Utility class used by global analysis for the calculations
	 */
	private static class GlobalSum {
		/**
		 * The sum of the words frequency
		 */
		private double wordsFreqSum;

		/**
		 * The sum of the number of documents
		 */
		private double nbDocsSum;

		/**
		 * The sum of the score of documents
		 */
		private double scoreDocsSum;

		/**
		 * Default constructor
		 * @param wordFreq the frequency of the word
		 * @param scoreDoc the score of the document (the page)
		 */
		public GlobalSum(double wordFreq, double scoreDoc) {
			wordsFreqSum = wordFreq;
			nbDocsSum = 1.0;
			scoreDocsSum = scoreDoc;
		}

		/**
		 * Increments the attributes
		 * @param wordFreq the frequency of the word
		 * @param scoreDoc the score of the document (the page)
		 */
		public void increment(double wordFreq, double scoreDoc) {
			wordsFreqSum += wordFreq;
			nbDocsSum += 1.0;
			scoreDocsSum += scoreDoc;
		}

		/**
		 * Getter of the sum of the words frequency
		 * @return the sum of the words frequency
		 */
		public double getWordsFreqSum() {
			return wordsFreqSum;
		}

		/**
		 * Getter of the sum of the number of documents
		 * @return the sum of the number of documents
		 */
		public double getNbDocsSum() {
			return nbDocsSum;
		}

		/**
		 * Getter of the sum of the score of documents
		 * @return the sum of the score of documents
		 */
		public double getScoreDocsSum() {
			return scoreDocsSum;
		}
	}
}
