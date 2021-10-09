package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class InternalCrawler extends RecursiveTask<Boolean> {
    private final String url;
    private final Instant deadline;
    private final int maxDepth;
    private final ConcurrentMap<String, Integer> counts;
    private final ConcurrentSkipListSet<String> visitedUrls;
    private final PageParserFactory pageParserFactory;
    private final Clock clock;
    private final List<Pattern> ignoredUrls;

    private InternalCrawler(String url, Instant deadline,
                           int maxDepth,
                           ConcurrentMap<String, Integer> counts,
                           ConcurrentSkipListSet<String> visitedUrls,
                           PageParserFactory pageParserFactory,
                           Clock clock,
                           List<Pattern> ignoredUrls) {
        this.url = url;
        this.deadline = deadline;
        this.maxDepth = maxDepth;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
        this.clock = clock;
        this.pageParserFactory = pageParserFactory;
        this.ignoredUrls = ignoredUrls;
    }

    @Override
    protected Boolean compute() {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return false;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return false;
            }
        }
        if (visitedUrls.contains(url)) {
            return false;
        }
        visitedUrls.add(url);
        PageParser.Result result = pageParserFactory.get(url).parse();

        for (ConcurrentMap.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : e.getValue() + v);
        }
        List<InternalCrawler> subtasks = new ArrayList<>();
        InternalCrawler.Builder builder = new InternalCrawler.Builder()
                .setClock(clock)
                .setCounts(counts)
                .setDeadline(deadline)
                .setIgnoredUrls(ignoredUrls)
                .setPageParserFactory(pageParserFactory)
                .setVisitedUrls(visitedUrls);
        for (String link : result.getLinks()) {
            subtasks.add(builder
                            .setUrl(link)
                            .setMaxDepth(maxDepth - 1)
                            .build());
        }
        invokeAll(subtasks);
        return true;
    }


    public static final class Builder {
        private String url;
        private Instant deadline;
        private int maxDepth;
        private ConcurrentMap<String, Integer> counts;
        private ConcurrentSkipListSet<String> visitedUrls;
        private PageParserFactory pageParserFactory;
        private Clock clock;
        private List<Pattern> ignoredUrls;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setDeadline(Instant deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder setCounts(ConcurrentMap<String, Integer> counts) {
            this.counts = counts;
            return this;
        }

        public Builder setVisitedUrls(ConcurrentSkipListSet<String> visitedUrls) {
            this.visitedUrls = visitedUrls;
            return this;
        }

        public Builder setPageParserFactory(PageParserFactory pageParserFactory) {
            this.pageParserFactory = pageParserFactory;
            return this;
        }

        public Builder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder setIgnoredUrls(List<Pattern> ignoredUrls) {
            this.ignoredUrls = ignoredUrls;
            return this;
        }

        public InternalCrawler build(){
            return new InternalCrawler(
                    url,
                    deadline,
                    maxDepth,
                    counts,
                    visitedUrls,
                    pageParserFactory,
                    clock,
                    ignoredUrls);
        }
    }

}

