MfCrawler - Mini Focused Crawler 
=================================
MfCrawler is a intuitive [Web crawler](http://en.wikipedia.org/wiki/Web_crawler).
You can easily configure the crawler by set up crawl parameters and keywords and by add domains
to a blacklist. Finally, the results can be analyzed and exported in different formats.

The purpose of this crawler is not to crawl the entire web but is to crawl a portion of the web.

Install and use
---------------
First, you need the Java Virtual Machine version 7 that you can download on
[java.com](http://java.com). Then, download the last version of MfCrawler and launch
the "Jar", no installation is required.

[Download : Mini Focused Crawler v1.0 - october 2013](http://lbertelo.free.fr/mfcrawler/mfcrawler_v1.0.jar)

Screenshots
-----------
![Screenshot 1](http://lbertelo.free.fr/mfcrawler/mfcrawler_screenshot_4.png)

![Screenshot 2](http://lbertelo.free.fr/mfcrawler/mfcrawler_screenshot_5.png)

Notes
-----
* For optimal crawl result, you need to crawl, analyze result, adapt filters and parameters,
restart the crawl and loop until you have a good result. 

* Some structures of sites like Wikis or social networks are difficult to crawl and analyze because
many links refer to the same content.

User Manual
-----------

### Menu

* Project
    * Clear database : Clear all the crawling information from current project.
    * New Project : Create a new project and the current project is saved.
    * Manage Project : Allow to load or delete projects.
    
* Configuration
    * Proxy : Allow to configure proxy settings
    * General : Allow to configure global settings that not depend on the crawl project.

### Tabbed Panes

##### Monitoring

The monitoring pane allow to configure the project and monitoring the crawler.

On this pane, you can start and stop the crawler, and you can see the detail of crawling 
threads. The crawler use several threads to speed up the crawling but between 2 requests
on a same site (a site is identified by its full domain name).

With the button "Manage starting pages", you have access to a dialog window that allow
to add starting pages from a file or from a text field.

##### Filters

There are 2 types of filters, the keywords and the blacklisted sites.
You can import and export this filters into a txt file.

The keywords are words associated with a score which can be negative or positive.
The keywords with their scores determine score's pages and sites.

The blacklisted sites which will not be crawled, are identified by their full domain name.
You can also blacklist a root domain or domain extension by prefixing the term with a dot.
By blacklisting a site, all pages of the site are deleted.

##### Overview

The overview pane allow to see the results from crawling. You can see the crawled pages,
the found pages, the errors and the redirect pages, and sort the results by name, score,
deep or crawl time.

By seeing the results, you can adjust the crawl configuration and the keywords, and you can
add domains to the blacklist.

##### Analyze

This pane allow to analyze the contents of crawled pages.
For each word, you can see :
* Weighted Tf * Idf : Idf * (sum of pages' scores which contain the term) / (sum of all pages' scores)
* Tf * Idf : Tf * Idf, with Idf = log(1.0 / (Document Frequency))
* Word frequency (in ‰) : Logarithmically scaled frequency Tf
* Document frequency (in  ‰) : Frequency of documents which contain the term

[see details of Tf*Idf on wikipedia](http://en.wikipedia.org/wiki/Tf%E2%80%93idf)

##### Export

With the export pane, you can export the results with a page scope or site scope into
a CSV file or a GEXF file.

GEXF file format is a XML file for describing networks structures, for more information
visit [gexf.net](http://gexf.net). Gephi is an opensource software that can read GEXF file
and allow to visualize and explore the associated networks, you can download Gephi on
[gephi.org](http://gephi.org/).

### Configuration

* Thread number : the number of thread which perform the crawl
* Inner deep : the deep from page to page in a same site.
* Outer deep : the deep from site to site.
* Crawl remaining number : the number of remaining crawl before to stop the crawler
* Minimum score : the minimum score for pages that are considered interesting
* Default crawl delay : the delay between 2 requests on a same site
(if the robots.txt doesn't contain crawl delay information)
* Force crawl despite robots.txt : allow to crawl pages despite disallow on robots.txt
* Crawl delay when crawl is forced : the delay between 2 requests if the page is not allowed

Pages can have many inner deeps and outer deeps because they can have many incoming links
but the only saved deep is the smaller outer deep found with the smaller inner deep found.
(The smaller outer deep overrides the inner deep)

### How to crawl

1. Add starting pages and, if possible add the first keywords. Set short limits to
crawl configuration especially for inner deep, outer deep and crawl remaining number.

2. Stop the crawler, look the result in the overview pane. Then, adjust keywords, add 
domains to the blacklist and extend the crawl configuration. Repeat this step
until you are satisfied. (if you derive from your goal, identify important pages,
clean the project and restart the process)

3. Visualize the result in the overview pane or export it into a file.

License
-------
Mini Focused Crawler is distributed under
[GNU General Public License v3](http://www.gnu.org/licenses/gpl.html).
