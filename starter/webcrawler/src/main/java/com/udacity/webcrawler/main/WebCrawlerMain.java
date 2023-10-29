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
    
//    if (config.getResultPath() != null) {
    if (!config.getResultPath().equals("") || (config.getResultPath() != null) ) {
    	System.out.println(config.getResultPath());
    	String fileName = config.getResultPath();
    	Path path = Path.of(fileName);
    	resultWriter.write(path);
    	// 
    	Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
//    	Writer out1 = new BufferedWriter(new OutputStreamWriter());
    	resultWriter.write(out);
    	System.out.println("true");
    } else {
    	System.out.println("false");
    }
    
    // TODO: Write the profile data to a text file (or System.out if the file name is empty)
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [starting-url]");
      return;
    }

    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
    // test 
    //src/main/config/sample_config_sequential.json
//    CrawlerConfiguration config = new ConfigurationLoader(Path.of("src/main/config/sample_config_sequential.json")).load();
    new WebCrawlerMain(config).run();
  }
}
