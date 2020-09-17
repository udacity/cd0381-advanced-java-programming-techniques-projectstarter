package com.udacity.webcrawler.parser;

import com.udacity.webcrawler.profiler.Profiled;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parses and processes remote and local HTML pages to return a parse {@link Result}.
 */
public interface PageParser {

  /**
   * Processes the HTML page and returns a {@link Result} for the page.
   */
  @Profiled
  Result parse();

  /**
   * A data class that represents the outcome of processing an HTML page.
   */
  final class Result {
    private final Map<String, Integer> wordCounts;
    private final List<String> links;

    private Result(Map<String, Integer> wordCounts, List<String> links) {
      this.wordCounts = Objects.requireNonNull(wordCounts);
      this.links = Objects.requireNonNull(links);
    }

    /**
     * Returns an unmodifiable {@link Map} containing the words and word frequencies encountered
     * when parsing the web page.
     */
    public Map<String, Integer> getWordCounts() {
      return wordCounts;
    }

    /**
     * Returns an unmodifiable {@link List} of the hyperlinks encountered when parsing the web page.
     */
    public List<String> getLinks() {
      return links;
    }

    /**
     * A builder class for the parse {@link Result}. This builder keeps track of word counts and
     * hyperlinks encountered while parsing a web page.
     */
    static final class Builder {
      private final Map<String, Integer> wordCounts = new HashMap<>();
      private final Set<String> links = new HashSet<>();

      /**
       * Increments the frequency counter for the given word.
       */
      void addWord(String word) {
        Objects.requireNonNull(word);
        wordCounts.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
      }

      /**
       * Adds the given link, if it has not already been added.
       */
      void addLink(String link) {
        links.add(Objects.requireNonNull(link));
      }

      /**
       * Constructs a {@link Result} from this builder.
       */
      Result build() {
        return new Result(
            Collections.unmodifiableMap(wordCounts),
            links.stream().collect(Collectors.toUnmodifiableList()));
      }
    }
  }
}
