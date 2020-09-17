package com.udacity.webcrawler.parser;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Guice dependency injection module that installs a {@link PageParserFactory} that can be used to
 * create page parsers.
 */
public final class ParserModule extends AbstractModule {
  private final Duration timeout;
  private final List<Pattern> ignoredWords;

  /**
   * Creates a {@link ParserModule} from the given timeout and ignored word patterns.
   */
  private ParserModule(Duration timeout, List<Pattern> ignoredWords) {
    this.timeout = timeout;
    this.ignoredWords = ignoredWords;
  }

  @Override
  protected void configure() {
    bind(Key.get(Duration.class, ParseDeadline.class)).toInstance(timeout);
    bind(new Key<List<Pattern>>(IgnoredWords.class) {}).toInstance(ignoredWords);
    bind(PageParserFactory.class).to(PageParserFactoryImpl.class);
  }

  /**
   * A builder class for {@link ParserModule}.
   */
  public static final class Builder {
    private Duration timeout;
    private List<Pattern> ignoredWords;

    /**
     * Sets the timeout that will be used by the page parser.
     */
    public Builder setTimeout(Duration timeout) {
      this.timeout = Objects.requireNonNull(timeout);
      return this;
    }

    /**
     * Sets the ignored word patterns that will be used by the page parser.
     */
    public Builder setIgnoredWords(List<Pattern> ignoredWords) {
      this.ignoredWords = Objects.requireNonNull(ignoredWords);
      return this;
    }

    /**
     * Builds a {@link ParserModule} from this {@link Builder}.
     */
    public ParserModule build() {
      return new ParserModule(timeout, ignoredWords);
    }
  }
}
