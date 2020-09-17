package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A fake, mutable {@link Clock} implementation for tests.
 */
public final class FakeClock extends Clock {

  private Instant now;
  private ZoneId zoneId;

  @Inject
  public FakeClock() {
    this(Instant.now(), ZoneId.systemDefault());
  }

  public FakeClock(Instant now, ZoneId zoneId) {
    this.now = Objects.requireNonNull(now);
    this.zoneId = Objects.requireNonNull(zoneId);
  }

  @Override
  public ZoneId getZone() {
    return zoneId;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return new FakeClock(now, zone);
  }

  @Override
  public Instant instant() {
    return now;
  }

  /**
   * Increments the time of the fake clock by the given amount.
   */
  public void tick(Duration duration) {
    now = now.plus(Objects.requireNonNull(duration));
  }

  /**
   * Sets the time of the fake clock.
   */
  public void setTime(Instant instant) {
    this.now = Objects.requireNonNull(instant);
  }

  /**
   * Sets the zone of the fake clock.
   */
  public void setZone(ZoneId zoneId) {
    this.zoneId = Objects.requireNonNull(zoneId);
  }
}
