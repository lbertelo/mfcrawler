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
 * Global Data analysis for all the crawled content
 * 
 * @author lbertelo
 */
public class GlobalAnalysis {

	/**
	 * The word
	 */
	private String word;

	/**
	 * The average frequency of the word per document (page)
	 */
	private Double wordFrequency;

	/**
	 * The frequency of document (page) which contains the word
	 */
	private Double docFrequency;

	/**
	 * tf * idf
	 */
	private Double tfIdf;

	/**
	 * Weighted tf * idf (weighted with the page's score)
	 */
	private Double weightedTfIdf;

	/**
	 * Default constructor
	 * @param word the word
	 * @param wordFrequency the word frequency
	 * @param docFrequency the doc frequency
	 * @param tfIdf tf * idf
	 * @param weightedTfIdf weighted tf * idf
	 */
	public GlobalAnalysis(String word, Double wordFrequency, Double docFrequency, Double tfIdf, Double weightedTfIdf) {
		this.word = word;
		this.wordFrequency = wordFrequency;
		this.docFrequency = docFrequency;
		this.tfIdf = tfIdf;
		this.weightedTfIdf = weightedTfIdf;
	}

	/**
	 * Getter of the word
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Getter of the word frequency
	 * @return the word frequency
	 */
	public Double getWordFrequency() {
		return wordFrequency;
	}

	/**
	 * Getter of the document frequency
	 * @return the document frequency
	 */
	public Double getDocFrequency() {
		return docFrequency;
	}

	/**
	 * Getter of tf * idf
	 * @return tf * idf
	 */
	public Double getTfIdf() {
		return tfIdf;
	}

	/**
	 * Getter of weighted tf * idf
	 * @return weighted tf * idf
	 */
	public Double getWeightedTfIdf() {
		return weightedTfIdf;
	}

}
