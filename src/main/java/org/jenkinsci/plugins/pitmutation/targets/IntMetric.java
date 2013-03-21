package org.jenkinsci.plugins.pitmutation.targets;

/**
 * @author Ed Kimber
 */
public class IntMetric extends AbstractMetric<Integer> {
  public IntMetric(Integer value) {
    super(value);
  }

//  public Class<Integer> getValueClass() {
//    return Integer.class;
//  }

  public Metric<Integer> aggregate(Metric<Integer> metric) {
    return new IntMetric(IntMetric.this.getValue() + metric.getValue());
  }

  public Metric<Integer> delta(Metric<Integer> metric) {
    return new IntMetric(IntMetric.this.getValue() - metric.getValue());
  }
}
