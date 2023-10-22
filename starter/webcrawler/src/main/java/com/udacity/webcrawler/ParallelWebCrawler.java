package com.udacity.webcrawler;

import com.google.common.collect.Lists;
import com.udacity.webcrawler.json.CrawlResult;

import com.udacity.webcrawler.json.CrawlResult.Builder;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;
import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {

  private final Clock clock;
  private final Duration timeout;
  private final int popularWordCount;
  private final ForkJoinPool pool;

  private final PageParserFactory parserFactory;
  private final int maxDepth;
  private final List<Pattern> ignoredUrls;

  @Inject
  ParallelWebCrawler(
      Clock clock,
      @Timeout Duration timeout,
      @PopularWordCount int popularWordCount,
      @TargetParallelism int threadCount,
      PageParserFactory parserFactory,
      @MaxDepth int maxDepth,
      @IgnoredUrls List<Pattern> ignoredUrls) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
    this.parserFactory = parserFactory;
    this.maxDepth = maxDepth;
    this.ignoredUrls = ignoredUrls;
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    Instant deadline = clock.instant().plus(timeout);
    ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
    ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
    CrawlTask crawlTask = new CrawlTask.Builder().setUrlsToCrawl(startingUrls)
        .setVisitedUrls(visitedUrls)
        .setClock(clock)
        .setDeadline(deadline)
        .setMaxDepth(maxDepth)
        .setCounts(counts)
        .setIgnoredUrls(ignoredUrls)
        .parserFactory(parserFactory).build();
    crawlTask.compute();
    pool.invoke(crawlTask);

    if (counts.isEmpty()) {
      return new CrawlResult.Builder()
          .setWordCounts(counts)
          .setUrlsVisited(visitedUrls.size())
          .build();
    }
    return new Builder().setUrlsVisited(visitedUrls.size())
        .setWordCounts(WordCounts.sort(counts, popularWordCount))
        .build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }

  public static class CrawlTask extends RecursiveAction {


    private final Clock clock;

    private final Instant deadline;


    private final int maxDepth;

    private final ConcurrentSkipListSet<String> visitedUrls;

    private final ConcurrentHashMap<String, Integer> counts;

    private final List<String> urlsToCrawl;

    private final PageParserFactory parserFactory;

    private final List<Pattern> ignoredUrls;

    public CrawlTask(
        Clock clock, Instant deadline,
        int maxDepth,
        ConcurrentSkipListSet<String> visitedUrls,
        ConcurrentHashMap<String, Integer> counts,
        List<String> urls,
        PageParserFactory parserFactory,
        List<Pattern> ignoredUrls) {
      this.clock = clock;
      this.deadline = deadline;
      this.maxDepth = maxDepth;
      this.counts = counts;
      this.visitedUrls = visitedUrls;
      this.urlsToCrawl = urls;
      this.parserFactory = parserFactory;
      this.ignoredUrls = ignoredUrls;
    }

    @Override
    protected void compute() {
      if (urlsToCrawl.size() <= 5) {
        // Process the URLs directly
        processUrls(urlsToCrawl);
      } else {
        // Split the task into smaller subtasks
        List<CrawlTask> subtasks = createSubtasks();

        // Invoke all subtasks in parallel
        invokeAll(subtasks);
      }
    }

    private void processUrls(
        List<String> urls) {
      for (String urlToCrawl : urls) {
        crawlInternal(urlToCrawl, deadline, maxDepth, counts, visitedUrls);
      }
    }

    private void crawlInternal(
        String url,
        Instant deadline,
        int maxDepth,
        Map<String, Integer> counts,
        Set<String> visitedUrls) {
      if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
        return;
      }
      for (Pattern pattern : ignoredUrls) {
        if (pattern.matcher(url).matches()) {
          return;
        }
      }
      if (visitedUrls.contains(url)) {
        return;
      }
      visitedUrls.add(url);
      PageParser.Result result = parserFactory.get(url).parse();
      for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
        if (counts.containsKey(e.getKey())) {
          counts.put(e.getKey(), e.getValue() + counts.get(e.getKey()));
        } else {
          counts.put(e.getKey(), e.getValue());
        }
      }
      for (String link : result.getLinks()) {
        crawlInternal(link, deadline, maxDepth - 1, counts, visitedUrls);
      }
    }

    private List<CrawlTask> createSubtasks() {
      List<CrawlTask> crawlTasks = new ArrayList<>();
      List<List<String>> batches = Lists.partition(urlsToCrawl, 5);
      for (List<String> batch : batches) {
        crawlTasks.add(new CrawlTask.Builder().setUrlsToCrawl(batch)
            .setVisitedUrls(visitedUrls)
            .setClock(clock)
            .setDeadline(deadline)
            .setMaxDepth(maxDepth)
            .setCounts(counts)
            .setIgnoredUrls(ignoredUrls)
            .parserFactory(parserFactory).build());
      }
      return crawlTasks;
      // Split the urlsToCrawl into smaller chunks
      // Create a new CrawlTask for each chunk
      // Return the list of subtasks
    }

    public static final class Builder {

      private Clock clock;

      private Instant deadline;

      private ConcurrentSkipListSet<String> visitedUrls;

      private ConcurrentHashMap<String, Integer> counts;

      private PageParserFactory parserFactory;

      private List<String> urlsToCrawl;

      private int maxDepth;

      private List<Pattern> ignoredUrls;

      // Other parameters as needed

      public Builder setUrlsToCrawl(List<String> urlsToCrawl) {
        this.urlsToCrawl = urlsToCrawl;
        return this;
      }

      public Builder parserFactory(PageParserFactory parserFactory) {
        this.parserFactory = parserFactory;
        return this;
      }

      public Builder setVisitedUrls(ConcurrentSkipListSet<String> visitedUrls) {
        this.visitedUrls = visitedUrls;
        return this;
      }

      public Builder setCounts(ConcurrentHashMap<String, Integer> counts) {
        this.counts = counts;
        return this;
      }

      public Builder setClock(Clock clock) {
        this.clock = clock;
        return this;
      }

      public Builder setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
      }

      public Builder setDeadline(Instant deadline) {
        this.deadline = deadline;
        return this;
      }

      public Builder setIgnoredUrls(List<Pattern> ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
        return this;
      }


      // Setters for other parameters
      public CrawlTask build() {
        return new CrawlTask(clock, deadline,  maxDepth,
            visitedUrls,
            counts, urlsToCrawl, parserFactory, ignoredUrls);
      }
    }
  }

}
