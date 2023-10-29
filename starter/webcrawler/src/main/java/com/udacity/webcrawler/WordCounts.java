package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.Multiset.Entry;

/**
 * Utility class that sorts the map of word counts.
 *
 * <p>TODO: Reimplement the sort() method using only the Stream API and lambdas and/or method
 *          references.
 */
final class WordCounts {

  /**
   * Given an unsorted map of word counts, returns a new map whose word counts are sorted according
   * to the provided {@link WordCountComparator}, and includes only the top
   * {@param popluarWordCount} words and counts.
   *
   * <p>TODO: Reimplement this method using only the Stream API and lambdas and/or method
   *          references.
   *
   * @param wordCounts       the unsorted map of word counts.
   * @param popularWordCount the number of popular words to include in the result map.
   * @return a map containing the top {@param popularWordCount} words and counts in the right order.
   */
  static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {

    // TODO: Reimplement this method using only the Stream API and lambdas and/or method references.
	  
	//PriorityQueue 
    PriorityQueue<Map.Entry<String, Integer>> sortedCounts =
        new PriorityQueue<>(wordCounts.size(), new WordCountComparator());
    
    sortedCounts.addAll(wordCounts.entrySet()); // mean get string from the map wordCounts
    
    //innit
    Map<String, Integer> topCounts = new LinkedHashMap<>();
    
    //for to check element 
    for (int i = 0; i < Math.min(popularWordCount, wordCounts.size()); i++) {
    	
      Map.Entry<String, Integer> entry = sortedCounts.poll();// poll() get head and delete this head
      
      //
      topCounts.put(entry.getKey(), entry.getValue());
    }
    return topCounts;
    
//    return wordCounts.entrySet().stream()
////    		.sorted((o1, o2)->o1.getItem().getValue().compareTo(o2.getItem().getValue()))
//    		.sorted((o1, o2) -> compare(o1, o2))
//    		.collect(Collectors.toMap(Map.Entry::getKey,
//                    e -> e.getValue()
//                    .stream()
//                    .mapToInt(Vote::getVoteValue)
//                    .sum()))
//    		;
  }
  
  static Map<String, Integer> supportFunc(String string, Integer stringInt,int sizeWordCounts, int popularWordCount) {
	  
	  
	  Map<String, Integer> topCounts = new LinkedHashMap<>();
	  for (int i = 0; i < Math.min(popularWordCount, sizeWordCounts); i++) {
	      //
	      topCounts.put(string, stringInt);
	  }
	  
	  return topCounts;
  }
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
  private static final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
      if (!a.getValue().equals(b.getValue())) {
        return b.getValue() - a.getValue();
      }
      if (a.getKey().length() != b.getKey().length()) {
        return b.getKey().length() - a.getKey().length();
      }
      /*
         this.compareTo(that)
         a negative int if this < that
		 0 if this == that
		 a positive int if this > that
       */
      return a.getKey().compareTo(b.getKey());
    }
  }

  private WordCounts() {
    // This class cannot be instantiated
  }
}