package org.jenkinsci.plugins.pitmutation.metrics;

import com.google.common.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ed Kimber
 */
public class MetricSet implements Metric<MetricSet> {

//  public MetricSet() {
//    super(null);
//  }

  public Set<MutationMetric> getMetricTypes() {
    return metricMap_.keySet();
  }

  public <T extends Metric<S>, S> T getMetric(MutationMetric<T,S> type) {
    return (T) metricMap_.get(type);
  }

  public <T extends Metric<S>, S> void put(MutationMetric<T, S> type, Metric<S> metric) {
    metricMap_.put(type, metric);
  }

  public MetricSet getValue() {
    return this;
  }

  public TypeToken<MetricSet> getType() {
    return null;
  }

  public Metric<MetricSet> aggregate(Metric<MetricSet> metric) {
    return null;
  }

  public Metric<MetricSet> delta(Metric<MetricSet> metric) {
    return null;
  }

  private Map<MutationMetric, Metric> metricMap_ = new HashMap<MutationMetric, Metric>();
}
