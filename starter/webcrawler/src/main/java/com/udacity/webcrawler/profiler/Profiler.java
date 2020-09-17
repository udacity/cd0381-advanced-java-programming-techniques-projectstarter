package com.udacity.webcrawler.profiler;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

/**
 * A utility that wraps an object that should be performance profiled.
 *
 * <p>The profiler aggregates information about profiled method calls, and how long they took. The
 * aggregate information can then be written to a file with {@link #writeData(Writer) writeData}.
 */
public interface Profiler {

  /**
   * Wraps the given delegate to have its methods profiled.
   *
   * @param klass    the class object representing the interface of the delegate.
   * @param delegate the object that should be profiled.
   * @param <T>      type of the delegate object, which must be an interface type. The interface
   *                 must have at least one of its methods annotated with the {@link Profiled}
   *                 annotation.
   * @return A wrapped version of the delegate that
   * @throws IllegalArgumentException if the given delegate does not have any methods annotated with
   *                                  the {@link Profiled} annotation.
   */
  <T> T wrap(Class<T> klass, T delegate);

  /**
   * Formats the profile data as a string and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the destination where the formatted data should be written.
   * @throws IOException if there was a problem writing the data to file.
   */
  void writeData(Path path) throws IOException;

  /**
   * Formats the profile data as a string and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the formatted data should be written.
   * @throws IOException if there was a problem writing the data.
   */
  void writeData(Writer writer) throws IOException;
}
