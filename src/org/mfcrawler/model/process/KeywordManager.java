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

package org.mfcrawler.model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mfcrawler.model.IPropertyName;
import org.mfcrawler.model.SwingPropertyChangeModel;
import org.mfcrawler.model.dao.iterator.PageDbIterator;
import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;
import org.mfcrawler.model.util.I18nUtil;

/**
 * Manages the keywords
 * 
 * @author lbertelo
 */
public final class KeywordManager {

	/**
	 * The minimum score for a page
	 */
	public static final int MIN_PAGE_SCORE = -1000;

	/**
	 * The maximum score for a page
	 */
	public static final int MAX_PAGE_SCORE = 1000;

	/**
	 * Regex which prefixes a word
	 */
	private static final String REGEX_PRE_WORD = "[\\p{Space}\\p{Punct}]?(";

	/**
	 * Regex which suffixes a word
	 */
	private static final String REGEX_POST_WORD = ")[\\p{Space}\\p{Punct}]?";

	/**
	 * Regex which represents any word
	 */
	private static final String REGEX_WORD = "([^\\p{Space}\\p{Punct}]+)";

	/**
	 * Keyword Map with word as a key and score as a value
	 */
	private static Map<String, Integer> keywordMap = new HashMap<String, Integer>();

	/**
	 * Private constructor
	 */
	private KeywordManager() {

	}

	/**
	 * Getter of keyword map
	 * @return the static keyword map
	 */
	public static synchronized Map<String, Integer> getKeywordMap() {
		return keywordMap;
	}

	/**
	 * Setter of the keyword map
	 * @param keywordMap the static keyword map
	 */
	public static synchronized void setKeywordMap(Map<String, Integer> newKeywordMap) {
		keywordMap = newKeywordMap;
	}

	/**
	 * Makes a regex from a word
	 * @param word the word
	 * @return the regex from the word
	 */
	public static String makeRegex(String word) {
		StringBuilder regex = new StringBuilder();
		regex.append(REGEX_PRE_WORD);
		regex.append(word);
		regex.append(REGEX_POST_WORD);
		return regex.toString();
	}

	/**
	 * Counts the occurrences of words in a content and adds the result to
	 * wordsOccur
	 * @param wordsOccur the map modified with words as keys and occurrences as
	 *            values
	 * @param content the content of a page
	 */
	public static void countOccurrences(Map<String, Integer> wordsOccur, String content) {
		Pattern pattern = Pattern.compile(REGEX_WORD, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String word = matcher.group(1).toLowerCase();
			Integer occurrence = wordsOccur.get(word);
			if (occurrence == null) {
				occurrence = 1;
			} else {
				occurrence++;
			}

			wordsOccur.put(word, occurrence);
		}
	}

	/**
	 * Calculates the score for a word with its occurrence and its weight
	 * @param occurrence the occurrence of the word
	 * @param weight the weight of the word
	 * @return the score calculated
	 */
	public static double calculate(int occurrence, int weight) {
		double score;
		if (occurrence <= 0) {
			// Score minimum
			score = 0.0;
		} else if (occurrence <= 100) {
			// Score Calculation = weight * ( 25 * log(x) + 50 )
			score = weight * (25.0 * Math.log10(occurrence) + 50.0);
		} else {
			// Score maximum
			score = weight * 100.0;
		}
		return score;
	}

	/**
	 * Calculates the score of a content
	 * @param content the content of a page
	 * @return the score calculated
	 */
	public static synchronized double calculateContent(String content) {
		double score = 0.0;

		for (String word : keywordMap.keySet()) {
			Pattern pattern = Pattern.compile(makeRegex(word), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			int occurrence = 0;
			while (matcher.find()) {
				occurrence++;
			}

			int weight = keywordMap.get(word);
			score += calculate(occurrence, weight);
		}

		return score;
	}

	/**
	 * Recalculates the scores of all the pages
	 * @param propertyChangeModel to notify the progression
	 */
	public static void recalculateAllPages(SwingPropertyChangeModel propertyChangeModel) {
		Set<Link> crawledLinks = new HashSet<Link>();
		Map<Link, Double> estimatedScoreLinks = new HashMap<Link, Double>();

		PageDAO pageDao = new PageDAO();
		pageDao.initAllScores();

		pageDao.setAutoCommit(false);
		PageDbIterator pageDbIterator = pageDao.getCrawledPagesWithContent();

		propertyChangeModel.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.recalculateScores.step2"));
		while (pageDbIterator.hasNext()) {
			Page page = pageDbIterator.next();
			double currentScore = 0;
			
			if (page.getContent() != null) {
				currentScore = calculateContent(page.getContent());
			}

			pageDao.updateScorePage(page.getLink(), currentScore);
			crawledLinks.add(page.getLink());
			estimatedScoreLinks.remove(page.getLink());

			pageDao.loadLinks(page, false);
			List<Link> outgoingLinkList = new ArrayList<Link>();
			outgoingLinkList.addAll(page.getOutgoingInternLinks());
			outgoingLinkList.addAll(page.getOutgoingExternLinks());
			for (Link outgoingLink : outgoingLinkList) {
				Double estimatedScore = estimatedScoreLinks.get(outgoingLink);
				if (!crawledLinks.contains(outgoingLink)
						&& (estimatedScore == null || estimatedScore.doubleValue() < currentScore)) {
					estimatedScoreLinks.put(outgoingLink, currentScore);
				}
			}
		}

		propertyChangeModel.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.recalculateScores.step3"));
		for (Link link : estimatedScoreLinks.keySet()) {
			pageDao.updateScorePage(link, estimatedScoreLinks.get(link));
		}

		pageDao.commit();
		pageDao.setAutoCommit(true);
	}

}
