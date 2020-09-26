package com.udacity.webcrawler.json;

import com.udacity.webcrawler.testing.CloseableStringWriter;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public final class CrawlResultWriterTest {
  @Test
  public void testBasicJsonFormatting() throws Exception {
    // We are using a LinkedHashMap because the iteration order of the map matters.
    Map<String, Integer> counts = new LinkedHashMap<>();
    counts.put("foo", 12);
    counts.put("bar", 1);
    counts.put("foobar", 98);
    CrawlResult result =
        new CrawlResult.Builder()
            .setUrlsVisited(17)
            .setWordCounts(counts)
            .build();

    CrawlResultWriter resultWriter = new CrawlResultWriter(result);
    CloseableStringWriter stringWriter = new CloseableStringWriter();
    resultWriter.write(stringWriter);
    assertWithMessage("Streams should usually be closed in the same scope where they were created")
        .that(stringWriter.isClosed())
        .isFalse();
    String written = stringWriter.toString();

    // The purpose of all the wildcard matchers (".*") is to make sure we allow the JSON output to
    // contain extra whitespace where it does not matter.
    Pattern expected =
        Pattern.compile(".*\\{" +
            ".*\"wordCounts\".*:.*\\{" +
            ".*\"foo\".*:12.*," +
            ".*\"bar\".*:.*1," +
            ".*\"foobar\".*:.*98" +
            ".*}.*,.*" +
            ".*\"urlsVisited\".*:.*17" +
            ".*}.*", Pattern.DOTALL);

    assertThat(written).matches(expected);
  }
}
