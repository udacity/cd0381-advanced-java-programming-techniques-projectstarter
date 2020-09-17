package com.udacity.webcrawler;

import com.udacity.webcrawler.profiler.Profiler;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A fake {@link Profiler} implementation that does nothing.
 */
public final class NoOpProfiler implements Profiler {

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);
    return Objects.requireNonNull(delegate);
  }

  @Override
  public void writeData(Path path) {
    Objects.requireNonNull(path);
  }

  @Override
  public void writeData(Writer writer) {
    Objects.requireNonNull(writer);
  }
}
