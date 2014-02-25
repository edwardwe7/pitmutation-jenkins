package org.jenkinsci.plugins.pitmutation.metrics;

/**
 * @author Ed Kimber
 */
public class IntPercentMetric extends AbstractMetric<IntPercentage> {
  public IntPercentMetric(IntPercentage value) {
    super(value);
  }

  public Metric<IntPercentage> aggregate(Metric<IntPercentage> metric) {
    Percentage<Integer> first = IntPercentMetric.this.getValue();
    Percentage<Integer> second = metric.getValue();
    return new IntPercentMetric(new IntPercentage(
            first.getNumerator().aggregate(second.getNumerator()),
            first.getDenominator().aggregate(second.getDenominator())));
  }

  public Metric<IntPercentage> delta(Metric<IntPercentage> metric) {
    Percentage<Integer> first = IntPercentMetric.this.getValue();
    Percentage<Integer> second = metric.getValue();
    return new IntPercentMetric(new IntPercentage(
            first.getNumerator().delta(second.getNumerator()),
            first.getDenominator().delta(second.getDenominator())));
  }
}
