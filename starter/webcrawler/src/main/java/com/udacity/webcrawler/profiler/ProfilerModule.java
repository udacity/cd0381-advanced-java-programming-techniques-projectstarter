package com.udacity.webcrawler.profiler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.time.Clock;

/**
 * Guice dependency injection module that installs a {@link Profiler} singleton.
 *
 * <p>Requires a {@link java.time.Clock} to already be bound.
 */
public final class ProfilerModule extends AbstractModule {
  @Provides
  @Singleton
  Profiler provideProfiler(Clock clock) {
    return new ProfilerImpl(clock);
  }
}
