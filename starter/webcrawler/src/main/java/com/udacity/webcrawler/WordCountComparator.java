package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.Map.Entry;

/**
 * A {@link Comparator} that sorts word count pairs correctly:
 *
 * <p>
 * <ol>
 *   <li>First sorting by word count, ranking more frequent words higher.</li>
 *   <li>Then sorting by word length, ranking longer words higher.</li>
 *   <li>Finally, breaking ties using alphabetical order.</li>
 * </ol>
 */
final class WordCountComparator implements Comparator<Entry<String, Integer>> {
  @Override
  public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {
    if (!a.getValue().equals(b.getValue())) {
      return b.getValue() - a.getValue();
    }
    if (a.getKey().length() != b.getKey().length()) {
      return b.getKey().length() - a.getKey().length();
    }
    return a.getKey().compareTo(b.getKey());
  }
}
