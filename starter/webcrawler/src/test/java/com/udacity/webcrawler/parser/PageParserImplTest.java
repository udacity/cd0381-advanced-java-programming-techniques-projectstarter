package com.udacity.webcrawler.parser;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;

public final class PageParserImplTest {

  private static final String DATA_DIR = System.getProperty("testDataDir");
  private final String testPage = Paths.get(DATA_DIR, "test-page.html").toUri().toString();

  @Test
  public void basicParsing() {
    PageParser.Result result = new PageParserImpl(testPage, Duration.ZERO, List.of()).parse();

    assertThat(result.getLinks())
        .containsExactly(Paths.get(DATA_DIR, "link-1.html").toUri().toString());
    assertThat(result.getWordCounts()).hasSize(9);
    assertThat(result.getWordCounts()).containsEntry("the", 2);
    assertThat(result.getWordCounts()).containsEntry("quick", 1);
    assertThat(result.getWordCounts()).containsEntry("brown", 1);
    assertThat(result.getWordCounts()).containsEntry("fox", 1);
    assertThat(result.getWordCounts()).containsEntry("jumped", 1);
    assertThat(result.getWordCounts()).containsEntry("over", 1);
    assertThat(result.getWordCounts()).containsEntry("lazy", 1);
    assertThat(result.getWordCounts()).containsEntry("dog", 1);
  }

  @Test
  public void parsingWithIgnoredWords() {
    PageParser.Result result =
        new PageParserImpl(testPage, Duration.ZERO, List.of(Pattern.compile("^...$"))).parse();

    assertThat(result.getLinks())
        .containsExactly(Paths.get(DATA_DIR, "link-1.html").toUri().toString());
    assertThat(result.getWordCounts()).hasSize(6);
    assertThat(result.getWordCounts()).containsEntry("quick", 1);
    assertThat(result.getWordCounts()).containsEntry("brown", 1);
    assertThat(result.getWordCounts()).containsEntry("jumped", 1);
    assertThat(result.getWordCounts()).containsEntry("over", 1);
    assertThat(result.getWordCounts()).containsEntry("lazy", 1);
  }
}