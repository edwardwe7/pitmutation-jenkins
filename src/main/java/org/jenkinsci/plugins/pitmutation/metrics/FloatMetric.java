package org.jenkinsci.plugins.pitmutation.metrics;

/**
 * @author Ed Kimber
 */
public class FloatMetric extends AbstractMetric<Float> {
  public FloatMetric(Float value) {
    super(value);
  }

  public Metric<Float> aggregate(Metric<Float> metric) {
    return new FloatMetric(FloatMetric.this.getValue() + metric.getValue());
  }

  public Metric<Float> delta(Metric<Float> metric) {
    return new FloatMetric(FloatMetric.this.getValue() - metric.getValue());
  }
}
