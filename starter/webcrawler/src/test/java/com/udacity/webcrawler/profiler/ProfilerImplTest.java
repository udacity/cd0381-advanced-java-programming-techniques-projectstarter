package com.udacity.webcrawler.profiler;

import com.udacity.webcrawler.testing.CloseableStringWriter;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProfilerImplTest {
  private final FakeClock clock = new FakeClock();
  private final Profiler profiler = new ProfilerImpl(clock);
  private final ProfiledInterfaceImpl delegate = new ProfiledInterfaceImpl(clock);

  @Test
  public void delegateHasNoMethodsAnnotated() {
    assertThrows(
        IllegalArgumentException.class,
        () -> profiler.wrap(NonProfiledInterface.class, new NonProfiledInterfaceImpl()),
        "Profiler.wrap() should throw an IllegalArgumentException if the wrapped interface does " +
            "not contain a @Profiled method.");
  }

  @Test
  public void testToString() {
    ProfiledInterface proxy = profiler.wrap(ProfiledInterface.class, delegate);

    assertWithMessage("The proxy should delegate toString() calls to the wrapped object.")
        .that(proxy.toString())
        .isEqualTo(delegate.toString());
  }

  @Test
  public void testHashCode() {
    ProfiledInterface proxy = profiler.wrap(ProfiledInterface.class, delegate);

    assertWithMessage("The proxy should delegate hashCode() calls to the wrapped object.")
        .that(proxy.hashCode())
        .isEqualTo(delegate.hashCode());
  }

  @Test
  public void testEquals() {
    ProfiledInterface proxy1 = profiler.wrap(ProfiledInterface.class, delegate);
    ProfiledInterface proxy2 = profiler.wrap(ProfiledInterface.class, delegate);

    assertThat(proxy1).isNotSameInstanceAs(delegate);
    assertThat(proxy1).isEqualTo(delegate);
    assertThat(delegate).isEqualTo(proxy1);

    assertWithMessage("Each call to Profiler.wrap() should create a new proxy object.")
        .that(proxy1)
        .isNotSameInstanceAs(proxy2);
    assertWithMessage("Two proxies should be equal if their wrapped objects are equal")
        .that(proxy1)
        .isEqualTo(proxy2);
    assertWithMessage("Two proxies should be equal if their wrapped objects are equal")
        .that(proxy2)
        .isEqualTo(proxy1);
  }

  @Test
  public void testNonObjectEquals() {
    ProfiledInterface proxy = profiler.wrap(ProfiledInterface.class, delegate);

    assertWithMessage("Incorrect equals() method was called")
        .that(proxy.equals("foo", "bar"))
        .isFalse();

    assertThat(delegate.wasFakeEqualsCalled()).isTrue();
  }

  @Test
  public void testBasicProfiling() throws Exception {
    ProfiledInterface proxy = profiler.wrap(ProfiledInterface.class, delegate);

    Instant beforeInvocation = clock.instant();

    assertWithMessage("The intercepted method did not forward the return value correctly")
        .that(proxy.profiled())
        .isEqualTo("profiled");
    Instant afterInvocation = clock.instant();
    assertWithMessage("Expected time to advance from invocation.")
        .that(beforeInvocation)
        .isLessThan(afterInvocation);

    // Run the method again a few more times to aggregate some data.
    proxy.profiled();
    proxy.profiled();

    CloseableStringWriter writer = new CloseableStringWriter();
    profiler.writeData(writer);
    assertWithMessage("Streams should usually be closed in the same scope where they were created")
        .that(writer.isClosed())
        .isFalse();
    String written = writer.toString();
    assertWithMessage("The profile data was not written or is incorrect")
        .that(written)
        .contains(
            "com.udacity.webcrawler.profiler.ProfilerImplTest$ProfiledInterfaceImpl#profiled");
    assertThat(written).contains("0m 3s 0ms");
  }

  @Test
  public void testDeclaredExceptionHandling() throws Exception {
    ProfiledInterface proxy = profiler.wrap(ProfiledInterface.class, delegate);

    Instant beforeInvocation = clock.instant();
    Throwable expected = assertThrows(
        Throwable.class,
        () -> proxy.throwSomething(new Throwable("expected exception")),
        "The method interceptor should forward exceptions thrown by the wrapped object");
    assertWithMessage("The proxy threw a different exception than was thrown by the wrapped object")
        .that(expected)
        .hasMessageThat()
        .isEqualTo("expected exception");

    Instant afterInvocation = clock.instant();
    assertWithMessage("Expected time to advance from invocation.")
        .that(beforeInvocation)
        .isLessThan(afterInvocation);

    CloseableStringWriter writer = new CloseableStringWriter();
    profiler.writeData(writer);
    assertWithMessage("Streams should usually be closed in the same scope where they were created")
        .that(writer.isClosed())
        .isFalse();
    String written = writer.toString();
    assertWithMessage("Profile data should still be recorded if an exception was thrown.")
        .that(written)
        .contains("com.udacity.webcrawler.profiler.ProfilerImplTest$ProfiledInterfaceImpl");
    assertThat(written).contains("0m 1s 0ms");
  }

  /**
   * A test interface that does not have any {@link Profiled} methods.
   */
  private interface NonProfiledInterface {
  }

  /**
   * Concrete implementation of {@link NonProfiledInterface}.
   */
  private static final class NonProfiledInterfaceImpl implements NonProfiledInterface {
  }

  /**
   * A test interface that has a method annotated with {@link Profiled}.
   */
  private interface ProfiledInterface {
    @Profiled
    String profiled();

    @Profiled
    void throwSomething(Throwable throwable) throws Throwable;

    boolean equals(String foo, String bar);
  }

  /**
   * Concrete implementation of {@link ProfiledInterface}.
   */
  private static final class ProfiledInterfaceImpl implements ProfiledInterface {
    private final FakeClock fakeClock;
    private boolean wasFakeEqualsCalled = false;

    ProfiledInterfaceImpl(FakeClock fakeClock) {
      this.fakeClock = Objects.requireNonNull(fakeClock);
    }

    @Override
    public String profiled() {
      fakeClock.tick(Duration.ofSeconds(1));
      return "profiled";
    }

    @Override
    public void throwSomething(Throwable throwable) throws Throwable {
      fakeClock.tick(Duration.ofSeconds(1));
      throw throwable;
    }

    @Override
    public boolean equals(Object other) {
      // All instances of ProfiledInterface are equal to one another.
      return (other instanceof ProfiledInterface);
    }

    @Override
    public boolean equals(String foo, String bar) {
      Objects.requireNonNull(foo);
      Objects.requireNonNull(bar);
      wasFakeEqualsCalled = true;
      return false;
    }

    public boolean wasFakeEqualsCalled() {
      return wasFakeEqualsCalled;
    }
  }
}
