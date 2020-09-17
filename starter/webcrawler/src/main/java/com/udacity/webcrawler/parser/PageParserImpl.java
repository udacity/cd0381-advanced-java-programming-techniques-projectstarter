package com.udacity.webcrawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Evaluator.Tag;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An implementation of {@link PageParser} that works for both local and remote files.
 *
 * <p>HTML parsing is done using the JSoup library. This class is a thin adapter around JSoup's API,
 * since JSoup does not know how to correctly resolve relative hyperlinks when parsing HTML from
 * local files.
 */
final class PageParserImpl implements PageParser {

  /**
   * Matches whitespace characters.
   */
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  /**
   * Matches non-word characters.
   */
  private static final Pattern NON_WORD_CHARACTERS = Pattern.compile("\\W");

  private final String uri;
  private final Duration timeout;
  private final List<Pattern> ignoredWords;

  /**
   * Constructs a page parser with the given parameters.
   *
   * @param uri          the URI of the file to parse.
   * @param timeout      the timeout to use when downloading the file, if it is remote.
   * @param ignoredWords patterns of which words should be ignored by the {@link #parse()} method.
   */
  PageParserImpl(String uri, Duration timeout, List<Pattern> ignoredWords) {
    this.uri = Objects.requireNonNull(uri);
    this.timeout = Objects.requireNonNull(timeout);
    this.ignoredWords = Objects.requireNonNull(ignoredWords);
  }

  @Override
  public Result parse() {
    URI parsedUri;
    try {
      parsedUri = new URI(uri);
    } catch (URISyntaxException e) {
      // Invalid link; ignore
      return new Result.Builder().build();
    }

    Document document;
    try {
      document = parseDocument(parsedUri);
    } catch (Exception e) {
      // There are multiple exceptions that can be encountered due to invalid URIs or Mimetypes that
      // Jsoup does not handle. There is not much we can do here.
      return new Result.Builder().build();
    }

    Result.Builder builder = new Result.Builder();
    // Do a single pass over the document to gather all hyperlinks and text.
    document.traverse(new NodeVisitor() {
      @Override
      public void head(Node node, int depth) {
        if (node instanceof TextNode) {
          String text = ((TextNode) node).text().strip();
          Arrays.stream(WHITESPACE.split(text))
              .filter(s -> !s.isBlank())
              .filter(s -> ignoredWords.stream().noneMatch(p -> p.matcher(s).matches()))
              .map(s -> NON_WORD_CHARACTERS.matcher(s).replaceAll(""))
              .map(String::toLowerCase)
              .forEach(builder::addWord);
          return;
        }
        if (!(node instanceof Element)) {
          return;
        }
        Element element = (Element) node;
        if (!element.is(new Tag("a")) || !element.hasAttr("href")) {
          return;
        }
        if (isLocalFile(parsedUri)) {
          // If this is a local file, add the base path back in manually, since Jsoup only knows how
          // to resolve relative hrefs if the base URI is a "real" remote URI.
          String basePath = Path.of(parsedUri).getParent().toString();
          builder.addLink(Path.of(basePath, element.attr("href")).toUri().toString());
        } else {
          // Otherwise, let Jsoup resolve the absolute URL for us.
          builder.addLink(element.attr("abs:href"));
        }
      }

      @Override
      public void tail(Node node, int depth) {
      }
    });
    return builder.build();
  }

  /**
   * Returns a Jsoup {@link Document} representation of the file at the given {@link URI}, which may
   * refer to a local document or a remote web page.
   */
  private Document parseDocument(URI uri) throws IOException {
    if (!isLocalFile(uri)) {
      return Jsoup.parse(uri.toURL(), (int) timeout.toMillis());
    }

    // Unfortunately, Jsoup.parse() has a baseUri parameter that does not work with local
    // "file://" URIs. If we want the parser to support those URIs, which are very useful for
    // testing, the work-around is to pass in an empty baseUri and manually add the base back to
    // href attributes.
    try (InputStream in = Files.newInputStream(Path.of(uri))) {
      return Jsoup.parse(in, StandardCharsets.UTF_8.name(), "");
    }
  }

  /**
   * Returns true if and only if the given {@link URI} represents a local file.
   */
  private static boolean isLocalFile(URI uri) {
    return uri.getScheme() != null && uri.getScheme().equals("file");
  }
}