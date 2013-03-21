package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.reflect.TypeToken;

/**
 * @author Ed Kimber
 */
public interface Metric<T> {
  public T getValue();

  public TypeToken<T> getType();

  public Metric<T> aggregate(final Metric<T> metric);

  public Metric<T> delta(final Metric<T> metric);
}
