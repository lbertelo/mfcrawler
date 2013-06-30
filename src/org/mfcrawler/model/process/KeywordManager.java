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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mfcrawler.model.dao.site.PageDAO;
import org.mfcrawler.model.dao.site.PageDbIterator;
import org.mfcrawler.model.pojo.site.Page;
import org.mfcrawler.model.pojo.site.link.Link;

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
	 * Calculate the score of a content
	 * @param content the content of a page
	 * @return the score calculated
	 */
	public synchronized Integer calculate(String content) {
		// FIXME améliorer la fonction calculate
		List<Integer> tmpScores = new ArrayList<Integer>();

		for (String word : keywordMap.keySet()) {
			Pattern pattern = Pattern.compile(makeRegex(word), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			int occurrence = 0;
			while (matcher.find()) {
				occurrence++;
			}

			int weight = keywordMap.get(word);
			tmpScores.add(occurrence * weight * 10);
		}

		if (!tmpScores.isEmpty()) {
			int addScores = 0;
			for (Integer score : tmpScores) {
				addScores += score;
			}
			return Math.round((addScores / tmpScores.size()));
		} else {
			return 0;
		}
	}

	/**
	 * Recalculate the scores of all the pages
	 */
	public void recalculateAll() {
		PageDAO pageDao = new PageDAO();
		pageDao.initAllScores();

		pageDao.setAutoCommit(false);
		PageDbIterator pageDbIterator = pageDao.getCompletePageList();
		while (pageDbIterator.hasNext()) {
			Page page = pageDbIterator.next();
			pageDao.loadAllLinks(page);

			Integer score = 0;
			if (page.getContent() != null) {
				score = calculate(page.getContent());
			}

			pageDao.updateScorePage(page.getLink(), score, true);

			List<Link> outgoingLinkList = new ArrayList<Link>();
			outgoingLinkList.addAll(page.getOutgoingInternLinks());
			outgoingLinkList.addAll(page.getOutgoingExternLinks());
			for (Link outgoingLink : outgoingLinkList) {
				pageDao.updateScorePage(outgoingLink, score, false);
			}
		}

		pageDao.commit();
		pageDao.setAutoCommit(true);
	}

}
