package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.profiler.Profiled;

import java.util.List;

/**
 * The main interface that defines the web crawler API.
 */
public interface WebCrawler {

  /**
   * Starts a crawl at the given URLs.
   *
   * @param startingUrls the starting points of the crawl.
   * @return the {@link CrawlResult} of the crawl.
   */
  @Profiled
  CrawlResult crawl(List<String> startingUrls);

  /**
   * Returns the maximum amount of parallelism (number of CPU cores) supported by this web crawler.
   */
  default int getMaxParallelism() {
    return 1;
  }
}
