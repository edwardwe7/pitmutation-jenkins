package org.jenkinsci.plugins.pitmutation.metrics;

import com.google.common.reflect.TypeToken;

/**
 * @author Ed Kimber
 */
public abstract class AbstractMetric<T> implements Metric<T> {
  final private T value_;

  public AbstractMetric(T value) {
    value_ = value;
  }

  public T getValue() {
    return value_;
  }

  public TypeToken<T> getType() {
    return new TypeToken<T>(getClass()) {};
  }
}
