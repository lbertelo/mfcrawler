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
	 * Regex which prefix a word
	 */
	private static final String REGEX_PRE_WORD = "[\\p{Space}\\p{Punct}](";

	/**
	 * Regex which suffix a word
	 */
	private static final String REGEX_POST_WORD = ")[\\p{Space}\\p{Punct}]";

	/**
	 * Unique instance of this class
	 */
	private static KeywordManager instance;

	/**
	 * Keyword Map with word as a key and score as a value
	 */
	private Map<String, Integer> keywordMap;

	/**
	 * Private constructor
	 */
	private KeywordManager() {
		keywordMap = new HashMap<String, Integer>();
	}

	/**
	 * Returns the unique instance of the keywordManager
	 * @return the unique instance
	 */
	public synchronized static KeywordManager get() {
		if (instance == null) {
			instance = new KeywordManager();
		}
		return instance;
	}

	/**
	 * Getter of keyword map
	 * @return the keyword map
	 */
	public Map<String, Integer> getKeywordMap() {
		return keywordMap;
	}

	/**
	 * Setter of the keyword map
	 * @param keywordMap the keyword map
	 */
	public void setKeywordMap(Map<String, Integer> keywordMap) {
		this.keywordMap = keywordMap;
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
	 * Calculates the score of a content
	 * @param content the content of a page
	 * @return the score calculated
	 */
	public synchronized double calculate(String content) {
		double score = 0.0;

		for (String word : keywordMap.keySet()) {

			Pattern pattern = Pattern.compile(makeRegex(word), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			int occurrence = 0;
			while (matcher.find()) {
				occurrence++;
			}

			int weight = keywordMap.get(word);

			if (occurrence <= 0) {
				// Score minimum
				score += 0.0;
			} else if (occurrence <= 100) {
				// Score Calculation = weight * ( 25 * log(x) + 50 )
				score += weight * (25.0 * Math.log10(occurrence) + 50.0);
			} else {
				// Score maximum
				score += weight * 100.0;
			}
		}

		return score;
	}

	/**
	 * Recalculates the scores of all the pages
	 * @param propertyChangeModel to notify the progression
	 */
	public void recalculateAll(SwingPropertyChangeModel propertyChangeModel) {
		Set<Link> crawledLinks = new HashSet<Link>();
		Map<Link, Double> estimatedScoreLinks = new HashMap<Link, Double>();

		PageDAO pageDao = new PageDAO();
		pageDao.initAllScores();

		pageDao.setAutoCommit(false);
		List<Page> crawledPageList = pageDao.getCrawledPagesForRecalculating();
		
		propertyChangeModel.notify(IPropertyName.LOADING, I18nUtil.getMessage("loading.recalculateScores.step2"));
		for (Page page : crawledPageList) {
			double currentScore = 0;
			if (page.getContent() != null) {
				currentScore = calculate(page.getContent());
			}

			pageDao.updateScorePage(page.getLink(), currentScore);
			crawledLinks.add(page.getLink());
			estimatedScoreLinks.remove(page.getLink());

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
