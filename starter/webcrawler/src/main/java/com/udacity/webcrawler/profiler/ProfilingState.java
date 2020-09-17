package com.udacity.webcrawler.profiler;

import java.io.Writer;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper class that records method performance data from the method interceptor.
 */
final class ProfilingState {
  // TODO: ArrayList is not a very good choice here. Replace this with a better choice of data
  //       structure.
  private final List<String> data = new ArrayList<>();

  /**
   * Records the given method invocation data.
   *
   * @param callingClass the Java class of the object that called the method.
   * @param method       the method that was called.
   * @param elapsed      the amount of time that passed while the method was called.
   */
  void record(Class<?> callingClass, Method method, Duration elapsed) {
    Objects.requireNonNull(callingClass);
    Objects.requireNonNull(method);
    Objects.requireNonNull(elapsed);
    // TODO: Complete this method implementation. This code is here as a place-holder.
    data.add(formatData(callingClass, method, elapsed));
  }

  /**
   * Writes the method invocation data to the given {@link Writer}.
   *
   * <p>Recorded data is aggregated across calls to the same method. For example, suppose
   * {@link #record(Class, Method, Duration) record} is called three times for the same method
   * {@code M()}, with each invocation taking 1 second. The total {@link Duration} reported by
   * this {@code write()} method for {@code M()} should be 3 seconds.
   */
  void write(Writer writer) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
    // TODO: Write the stored data to file. Each method should get its own line that contains the
    //       amount of time that was spent calling that method. If a method is called multiple times
    //       it all counts toward the same bucket of time.
  }

  /**
   * Formats a piece of profile data.
   */
  private static String formatData(Class<?> callingClass, Method method, Duration elapsed) {
    String line =
        String.format(
            "%s took %s", formatMethodCall(callingClass, method), formatDuration(elapsed));
    return line + System.lineSeparator();
  }

  /**
   * Formats the given method call for writing to a text file.
   *
   * @param callingClass the Java class of the object whose method was invoked.
   * @param method       the Java method that was invoked.
   * @return a string representation of the method call.
   */
  private static String formatMethodCall(Class<?> callingClass, Method method) {
    return String.format("%s#%s", callingClass.getName(), method.getName());
  }

  /**
   * Formats the given {@link Duration} for writing to a text file.
   */
  private static String formatDuration(Duration duration) {
    return String.format(
        "%sm %ss %sms", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart());
  }
}
