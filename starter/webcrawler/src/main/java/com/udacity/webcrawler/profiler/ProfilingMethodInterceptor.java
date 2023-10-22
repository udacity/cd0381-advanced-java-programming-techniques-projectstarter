package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Object targetObject;

  private final ProfilingState profilingState;

  private final Clock clock;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Object targetObject, Clock clock, ProfilingState profilingState) {
    this.targetObject = targetObject;
    this.clock = Objects.requireNonNull(clock);
    this.profilingState = profilingState;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    Instant startTime;
    Object result;
    if (method.isAnnotationPresent(Profiled.class)) {
      startTime = clock.instant();
      try {
        result = method.invoke(targetObject, args);
        return result;
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      } finally {
        Instant endTime = clock.instant();
        Duration duration = Duration.between(startTime, endTime);
        profilingState.record(targetObject.getClass(), method, duration);
      }
    }
    return method.invoke(targetObject, args);
  }

  @Override
  public boolean equals(Object object) {

    if (object == this) {
      return true;
    }
    if (!(object instanceof ProfilingMethodInterceptor)) {
      return false;
    }
    ProfilingMethodInterceptor methodInterceptor = (ProfilingMethodInterceptor) object;
    return Objects.equals(this.clock, methodInterceptor.clock) && Objects.equals(this.targetObject,
        methodInterceptor.targetObject) && Objects.equals(this.profilingState,
        methodInterceptor.profilingState);

  }
}
