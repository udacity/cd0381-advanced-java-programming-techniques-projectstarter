package com.udacity.webcrawler.testing;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A {@link StringWriter} that checks if it has already been closed.
 *
 * <p>For other kinds of writers, this could be tested by checking whether {@link #close()} throws
 * an {@link IOException}, but for {@link StringWriter}s the {@link #close()} method does nothing.
 */
public final class CloseableStringWriter extends StringWriter {
  private boolean closed = false;

  @Override
  public void close() throws IOException {
    if (closed) {
      throw new IOException("stream is closed");
    }
    closed = true;
  }

  /**
   * Returns whether this writer has been closed.
   */
  public boolean isClosed() {
    return closed;
  }
}