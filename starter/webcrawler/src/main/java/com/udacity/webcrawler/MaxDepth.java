package com.udacity.webcrawler;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for the max allowed depth of the web crawl.
 *
 * <p>The value bound to this annotation is the value of the {@code "maxDepth"} option from the
 * crawler configuration JSON.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxDepth {
}
