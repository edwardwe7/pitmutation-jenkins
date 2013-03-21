package org.jenkinsci.plugins.pitmutation.targets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ed Kimber
 */
public class Metrics {
  private Map<MutationMetric, Metric> metricMap_ = new HashMap<MutationMetric, Metric>();

  public Set<MutationMetric> getMetricTypes() {
    return metricMap_.keySet();
  }

  public Metric getMetric(MutationMetric metric) {
    return metricMap_.get(metric);
  }

  public void put(MutationMetric type, Metric metric) {
    metricMap_.put(type, metric);
  }
}
