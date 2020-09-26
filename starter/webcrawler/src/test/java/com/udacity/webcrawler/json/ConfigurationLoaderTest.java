package com.udacity.webcrawler.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public final class ConfigurationLoaderTest {

  @Test
  public void testBasicJsonConversion() {
    String json = "{ " +
        "\"startPages\": [\"http://example.com\", \"http://example.com/foo\"], " +
        "\"ignoredUrls\": [\"http://example\\\\.com/.*\"], " +
        "\"ignoredWords\": [\"^.{1,3}$\"], " +
        "\"parallelism\": 4, " +
        "\"implementationOverride\": \"fully.qualified.OverrideClass\", " +
        "\"maxDepth\": 100, " +
        "\"timeoutSeconds\": 10, " +
        "\"popularWordCount\": 5, " +
        "\"profileOutputPath\": \"profileOutput.txt\", " +
        "\"resultPath\": \"resultPath.json\" " +
        " }";

    Reader reader = new StringReader(json);
    CrawlerConfiguration config = ConfigurationLoader.read(reader);
    try {
      assertThat(reader.ready()).isTrue();
    } catch (IOException e) {
      fail("Streams should usually be closed in the same scope where they were created", e);
    }

    assertThat(config.getStartPages())
        .containsExactly("http://example.com", "http://example.com/foo").inOrder();
    assertThat(config.getIgnoredUrls()).hasSize(1);
    assertThat(config.getIgnoredUrls().get(0).pattern()).isEqualTo("http://example\\.com/.*");
    assertThat(config.getIgnoredWords()).hasSize(1);
    assertThat(config.getIgnoredWords().get(0).pattern()).isEqualTo("^.{1,3}$");
    assertThat(config.getParallelism()).isEqualTo(4);
    assertThat(config.getImplementationOverride()).isEqualTo("fully.qualified.OverrideClass");
    assertThat(config.getMaxDepth()).isEqualTo(100);
    assertThat(config.getTimeout()).isEqualTo(Duration.ofSeconds(10));
    assertThat(config.getPopularWordCount()).isEqualTo(5);
    assertThat(config.getProfileOutputPath()).isEqualTo("profileOutput.txt");
    assertThat(config.getResultPath()).isEqualTo("resultPath.json");
  }

  @Test
  public void testOptionalOptions() {
    // Same as above, but without any explicit implementationOverride or parallelism.
    String json = "{ " +
        "\"maxDepth\": 100, " +
        "\"timeoutSeconds\": 10, " +
        "\"popularWordCount\": 5 " +
        " }";

    Reader reader = new StringReader(json);
    CrawlerConfiguration config = ConfigurationLoader.read(reader);
    try {
      assertThat(reader.ready()).isTrue();
    } catch (IOException e) {
      fail("Streams should usually be closed in the same scope where they were created", e);
    }

    assertThat(config.getStartPages()).isEmpty();
    assertThat(config.getIgnoredUrls()).isEmpty();
    assertThat(config.getIgnoredWords()).isEmpty();
    assertThat(config.getParallelism()).isEqualTo(-1);
    assertThat(config.getImplementationOverride()).isEmpty();
    assertThat(config.getMaxDepth()).isEqualTo(100);
    assertThat(config.getTimeout()).isEqualTo(Duration.ofSeconds(10));
    assertThat(config.getPopularWordCount()).isEqualTo(5);
    assertThat(config.getProfileOutputPath()).isEmpty();
    assertThat(config.getResultPath()).isEmpty();
  }
}