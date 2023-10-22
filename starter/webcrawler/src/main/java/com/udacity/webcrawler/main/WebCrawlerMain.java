package com.udacity.webcrawler.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import java.io.IOException;
import java.nio.file.Paths;
import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;
import org.jsoup.internal.StringUtil;

public final class WebCrawlerMain {
  static String message = "asdasdas";
  static {
    System.out.println(message);
  }
  private final CrawlerConfiguration config;

  private WebCrawlerMain(CrawlerConfiguration config) {
    this.config = Objects.requireNonNull(config);
  }

  @Inject
  private WebCrawler crawler;

  @Inject
  private Profiler profiler;

  private void run() throws Exception {
    Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule()).injectMembers(this);
    CrawlResult result = crawler.crawl(config.getStartPages());
    CrawlResultWriter resultWriter = new CrawlResultWriter(result);
    if (config.getResultPath().isEmpty()){
      resultWriter.writeToStandardOutput(result);
    }
    else {
      resultWriter.write(Paths.get(config.getResultPath()));
    }
    // TODO: Write the crawl results to a JSON file (or System.out if the file name is empty)
    // TODO: Write the profile data to a text file (or System.out if the file name is empty)

    if (config.getResultPath().isEmpty()){
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        objectMapper.writeValue(System.out, Paths.get(config.getResultPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      profiler.writeData(Paths.get(config.getProfileOutputPath()));
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [starting-url]");
      return;
    }

    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
    new WebCrawlerMain(config).run();
  }
}
