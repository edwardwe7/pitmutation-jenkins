package org.jenkinsci.plugins.pitmutation.targets;

/**
 * @author Ed Kimber
 */
public abstract class PercentMetric<T> extends AbstractMetric<Percentage<T>> {
  public PercentMetric(Percentage<T> value) {
    super(value);
  }

  public abstract Metric<Percentage<T>> aggregate(Metric<Percentage<T>> metric);

  public abstract Metric<Percentage<T>> delta(Metric<Percentage<T>> metric);
}
