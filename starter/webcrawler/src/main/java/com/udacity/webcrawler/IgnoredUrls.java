package com.udacity.webcrawler;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for the crawler's list of ignored URL patterns.
 *
 * <p>The value bound to this annotation is the value of the {@code "ignoredUrl"} option from the
 * crawler configuration JSON.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoredUrls {
}
