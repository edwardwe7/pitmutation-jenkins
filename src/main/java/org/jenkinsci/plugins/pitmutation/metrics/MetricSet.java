package org.jenkinsci.plugins.pitmutation.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ed Kimber
 */
public class MetricSet extends AbstractMetric<Metric> {

  public MetricSet(Metric value) {
    super(value);
  }

  public Set<MutationMetric> getMetricTypes() {
    return metricMap_.keySet();
  }

  public <T extends Metric<S>,S> T getMetric(MutationMetric<T,S> type) {
    return (T) metricMap_.get(type);
  }

  public void put(MutationMetric type, Metric metric) {
    metricMap_.put(type, metric);
  }

  public Metric<Metric> aggregate(Metric<Metric> metricMetric) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Metric<Metric> delta(Metric<Metric> metricMetric) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private Map<MutationMetric, Metric> metricMap_ = new HashMap<MutationMetric, Metric>();

}
