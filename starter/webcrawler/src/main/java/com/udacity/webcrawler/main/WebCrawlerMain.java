package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

public final class WebCrawlerMain {

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
    // TODO: Write the crawl results to a JSON file (or System.out if the file name is empty)
    String resultPath = config.getResultPath();
    if(!resultPath.isEmpty()){
      Path path = Path.of(resultPath);
      resultWriter.write(path);
    } else{
      try(Writer out = new BufferedWriter(new OutputStreamWriter(System.out))) {
        resultWriter.write(out);
      }
    }

    // TODO: Write the profile data to a text file (or System.out if the file name is empty)
    String profileOutputPath = config.getProfileOutputPath();
    if(!profileOutputPath.isEmpty()){
      Path path = Path.of(profileOutputPath);
      profiler.writeData(path);
    } else {
      try(Writer out = new BufferedWriter(new OutputStreamWriter(System.out))){
        profiler.writeData(out);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [starting-url]");
      return;
    }

    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
//    CrawlerConfiguration config = new ConfigurationLoader(Path.of("/Users/ayush/Downloads/WebCrawler/nd079-c2-advanced-java-programming-techniques-projectstarter/starter/webcrawler/src/main/config/sample_config.json")).load();
    new WebCrawlerMain(config).run();
  }
}
