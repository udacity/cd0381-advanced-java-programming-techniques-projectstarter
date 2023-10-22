package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {

  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) throws IOException {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path);
    write(path, result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) throws IOException {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
    write(writer, result);
  }

  public static void write(Path path, CrawlResult crawlResult) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    objectMapper.writeValue(path.toFile(), crawlResult);
  }

  public static void write(Writer writer, CrawlResult crawlResult) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    objectMapper.writeValue(writer, crawlResult);
  }

  public void writeToStandardOutput(CrawlResult crawlResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writeValue(System.out, crawlResult);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
