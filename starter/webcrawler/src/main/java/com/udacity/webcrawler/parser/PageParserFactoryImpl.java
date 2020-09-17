package com.udacity.webcrawler.parser;

import com.udacity.webcrawler.Timeout;
import com.udacity.webcrawler.profiler.Profiler;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A {@link PageParserFactory} that wraps its returned instances using a {@link Profiler}.
 */
final class PageParserFactoryImpl implements PageParserFactory {
  private final Profiler profiler;
  private final List<Pattern> ignoredWords;
  private final Duration timeout;

  @Inject
  PageParserFactoryImpl(
      Profiler profiler, @IgnoredWords List<Pattern> ignoredWords, @Timeout Duration timeout) {
    this.profiler = profiler;
    this.ignoredWords = ignoredWords;
    this.timeout = timeout;
  }

  @Override
  public PageParser get(String url) {
    // Here, parse the page with the initial timeout (instead of just the time remaining), to make
    // the download less likely to fail. Deadline enforcement should happen at a higher level.
    PageParser delegate = new PageParserImpl(url, timeout, ignoredWords);
    return profiler.wrap(PageParser.class, delegate);
  }
}
