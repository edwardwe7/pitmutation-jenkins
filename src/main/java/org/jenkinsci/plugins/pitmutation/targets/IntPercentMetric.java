package org.jenkinsci.plugins.pitmutation.targets;

/**
 * @author Ed Kimber
 */
public class IntPercentMetric extends PercentMetric<Integer> {
  public IntPercentMetric(IntPercentage value) {
    super(value);
  }

  @Override
  public Metric<Percentage<Integer>> aggregate(Metric<Percentage<Integer>> metric) {
    Percentage<Integer> first = IntPercentMetric.this.getValue();
    Percentage<Integer> second = metric.getValue();
    return new IntPercentMetric(new IntPercentage(
            first.getNumerator().aggregate(second.getNumerator()),
            first.getDenominator().aggregate(second.getDenominator())));
  }

  @Override
  public Metric<Percentage<Integer>> delta(Metric<Percentage<Integer>> metric) {
    Percentage<Integer> first = IntPercentMetric.this.getValue();
    Percentage<Integer> second = metric.getValue();
    return new IntPercentMetric(new IntPercentage(
            first.getNumerator().delta(second.getNumerator()),
            first.getDenominator().delta(second.getDenominator())));
  }
}
