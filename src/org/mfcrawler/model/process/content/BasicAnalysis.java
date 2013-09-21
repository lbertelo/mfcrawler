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
 * Basic Data analysis (used for a site)
 * 
 * @author lbertelo
 */
public class BasicAnalysis {

	/**
	 * The word
	 */
	private String word;

	/**
	 * The occurrence of the word
	 */
	private Integer occurrence;

	/**
	 * The weight of the word
	 */
	private Integer weight;

	/**
	 * Default constructor
	 * @param word the word
	 * @param occurrence the occurrence of the word
	 * @param weight the weight of the word
	 */
	public BasicAnalysis(String word, Integer occurrence, Integer weight) {
		this.word = word;
		this.occurrence = occurrence;
		this.weight = weight;
	}

	/**
	 * Getter of the word
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Getter of the occurrence
	 * @return the occurrence
	 */
	public Integer getOccurrence() {
		return occurrence;
	}

	/**
	 * Getter of the weight
	 * @return the weight
	 */
	public Integer getWeight() {
		return weight;
	}

}
