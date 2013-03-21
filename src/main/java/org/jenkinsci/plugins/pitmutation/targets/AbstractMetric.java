package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.reflect.TypeToken;

/**
 * @author Ed Kimber
 */
public abstract class AbstractMetric<T> implements Metric<T> {
  final private T value_;
//  final private Class<T> metricTypeClass_;

  public AbstractMetric(T value) {
    value_ = value;
//    metricTypeClass_ = metricTypeClass;
  }

  public T getValue() {
    return value_;
  }

  public TypeToken<T> getType() {
    return new TypeToken<T>(getClass()) {};
  }

//  public Class<T> getMetricType() {
//    return metricTypeClass_;
//  }
}
