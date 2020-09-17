package com.udacity.webcrawler;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for the number of CPU cores specified in the crawler configuration.
 *
 * <p>The value bound to this annotation is a Java duration based on the {@code "timeoutSeconds"}
 * option from the crawler configuration JSON.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetParallelism {
}
