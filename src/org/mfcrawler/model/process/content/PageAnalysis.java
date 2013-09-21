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

/**
 * Data analysis for a page
 * 
 * @author lbertelo
 */
public class PageAnalysis extends BasicAnalysis {

	/**
	 * The score of the word
	 */
	private Long score;

	/**
	 * Default constructor
	 * @param word the word
	 * @param occurrence the occurrence of the word
	 * @param weight the weight of the word
	 */
	public PageAnalysis(String word, Integer occurrence, Integer weight) {
		super(word, occurrence, weight);

		if (weight != null) {
			this.score = Math.round(KeywordManager.calculate(occurrence, weight));
		}
	}

	/**
	 * Getter of the score
	 * @return the score
	 */
	public Long getScore() {
		return score;
	}

}
