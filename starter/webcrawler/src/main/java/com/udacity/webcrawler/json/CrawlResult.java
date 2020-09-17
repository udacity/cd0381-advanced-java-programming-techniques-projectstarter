package com.udacity.webcrawler.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Data class representing the final result of a web crawl.
 */
public final class CrawlResult {

  private final Map<String, Integer> wordCounts;
  private final int urlsVisited;

  /**
   * Constructs a {@link CrawlResult} with the given word counts and visited URL count.
   */
  private CrawlResult(Map<String, Integer> wordCounts, int urlsVisited) {
    this.wordCounts = wordCounts;
    this.urlsVisited = urlsVisited;
  }

  /**
   * Returns an unmodifiable {@link Map}. Each key in the map is a word that was encountered
   * during the web crawl. Each value is the total number of times a word was seen.
   *
   * <p>When computing these counts for a given crawl, results from the same page are never
   * counted twice.
   *
   * <p>The size of the returned map is the same as the {@code "popularWordCount"} option in the
   * crawler configuration. For example,  if {@code "popularWordCount"} is 3, only the top 3 most
   * frequent words are returned.
   *
   * <p>If multiple words have the same frequency, prefer longer words rank higher. If multiple
   * words have the same frequency and length, use alphabetical order to break ties (the word that
   * comes first in the alphabet ranks higher).
   */
  public Map<String, Integer> getWordCounts() {
    return wordCounts;
  }

  /**
   * Returns the number of distinct URLs the web crawler visited.
   *
   * <p>A URL is considered "visited" if the web crawler attempted to crawl that URL, even if the
   * HTTP request to download the page returned an error.
   *
   * <p>When computing this value for a given crawl, the same URL is never counted twice.
   */
  public int getUrlsVisited() {
    return urlsVisited;
  }

  /**
   * A package-private builder class for constructing web crawl {@link CrawlResult}s.
   */
  public static final class Builder {
    private Map<String, Integer> wordFrequencies = new HashMap<>();
    private int pageCount;

    /**
     * Sets the word counts. See {@link #getWordCounts()}
     */
    public Builder setWordCounts(Map<String, Integer> wordCounts) {
      this.wordFrequencies = Objects.requireNonNull(wordCounts);
      return this;
    }

    /**
     * Sets the total number of URLs visited. See {@link #getUrlsVisited()}.
     */
    public Builder setUrlsVisited(int pageCount) {
      this.pageCount = pageCount;
      return this;
    }

    /**
     * Constructs a {@link CrawlResult} from this builder.
     */
    public CrawlResult build() {
      return new CrawlResult(Collections.unmodifiableMap(wordFrequencies), pageCount);
    }
  }
}