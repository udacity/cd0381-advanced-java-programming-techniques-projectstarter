package com.udacity.webcrawler.parser;

/**
 * A factory interface that supplies instances of {@link PageParser} that have common parameters
 * (such as the timeout and ignored words) preset from injected values.
 */
public interface PageParserFactory {

  /**
   * Returns a {@link PageParser} that parses the given {@link url}.
   */
  PageParser get(String url);
}
