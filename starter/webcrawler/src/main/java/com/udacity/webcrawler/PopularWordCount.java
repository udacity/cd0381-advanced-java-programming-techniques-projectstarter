package com.udacity.webcrawler;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for the number of popular words the web crawler should output.
 *
 * <p>The value bound to this annotation is the value of the {@code "popularWordCount"} option from
 * the crawler configuration JSON.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface PopularWordCount {
}
