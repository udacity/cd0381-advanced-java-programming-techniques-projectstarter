package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.concurrent.ForkJoinTask.invokeAll;

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
  private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
  private final Map<String, Integer> counts = new ConcurrentHashMap<>();
  private final Instant deadline;
  private final Integer depth;
  private final List<Pattern> ignoredUrls;

  @Inject
  ParallelWebCrawler(
      Clock clock,
      PageParserFactory parserFactory,
      @Timeout Duration timeout,
      @PopularWordCount int popularWordCount,
      @MaxDepth int maxDepth,
      @IgnoredUrls List<Pattern> ignoredUrls,
      @TargetParallelism int threadCount) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
    this.parserFactory = parserFactory;
    this.depth = maxDepth;
    this.deadline = clock.instant().plus(this.timeout);
    this.ignoredUrls = ignoredUrls;
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    List<CrawlRecursiveTask> tasks = startingUrls.parallelStream().map(
                    x->new CrawlRecursiveTask(x, depth)).
            toList();

    for(CrawlRecursiveTask task: tasks){
      pool.invoke(task);
    }
    pool.shutdown();
    try {
      pool.awaitTermination(10, TimeUnit.SECONDS);
    }catch (InterruptedException interruptedException){

    }
    if(counts.isEmpty()){
      return new CrawlResult.Builder()
              .setWordCounts(counts)
              .setUrlsVisited(visitedUrls.size())
              .build();
    }
    System.out.println("counts "+counts);
    System.out.println("popularWordCount "+popularWordCount);
    return new CrawlResult.Builder().
            setUrlsVisited(visitedUrls.size()).
            setWordCounts(WordCounts.sort(counts, popularWordCount)).
            build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }

  private final class CrawlRecursiveTask extends RecursiveAction {

    private final String url;
    private final Integer depth;

    private CrawlRecursiveTask(String url, Integer depth){
      this.url = url;
      this.depth = depth;
    }

    private boolean processTask(){
      if(depth == 0
              || clock.instant().isAfter(deadline)
              ||visitedUrls.contains(url)
      ){
        return true;
      }
      for(Pattern pattern: ignoredUrls){
        if(pattern.matcher(url).matches()){
          return true;
        }
      }
      return false;
    }
    @Override
    protected void compute() {
      if(processTask()){
        return;
      }
      visitedUrls.add(url);
      PageParser.Result result = parserFactory.get(url).parse();
      for(Map.Entry<String, Integer> entry: result.getWordCounts().entrySet()){
        counts.put(entry.getKey(),
                counts.containsKey(entry.getKey())?
                        counts.get(entry.getKey())+entry.getValue():
                        entry.getValue()
                );
      }

      List<CrawlRecursiveTask> subtasks = result.getLinks().parallelStream().map(
              x->new CrawlRecursiveTask(x, depth-1)).collect(Collectors.toList());
      invokeAll(subtasks);
    }
  }
}
