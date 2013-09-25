package org.jenkinsci.plugins.pitmutation.target;

import org.jenkinsci.plugins.pitmutation.metrics.IntPercentMetric;
import org.jenkinsci.plugins.pitmutation.metrics.IntPercentage;
import org.jenkinsci.plugins.pitmutation.metrics.Metric;
import org.jenkinsci.plugins.pitmutation.metrics.MutatorMetric;
import org.jenkinsci.plugins.pitmutation.metrics.MutationMetric;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ed Kimber
 */
public class MetricTest {

  @Test
  public void testIntMetric() {
    assertThat(undetected(7).getValue(), is(7));
    assertThat(undetected(4).aggregate(undetected(7)).getValue(), is(11));
    assertThat(undetected(4).delta(undetected(7)).getValue(), is(-3));
  }

  @Test
  public void metricCalculatesKillRatioPercentage() {
    assertThat(killRatio(undetected(3), totalMutations(7))
            .getValue().getPercentage(),
            is(3f/7f * 100f));
  }

  @Test
  public void killRatioMetricCanAggregateMetrics() {
    Metric<IntPercentage> aggregate = killRatio(undetected(3), totalMutations(7))
            .aggregate(killRatio(undetected(7), totalMutations(11)));
    assertThat(aggregate.getValue().getPercentage(), is(10f/18f * 100f));
  }

  @Test
  public void killRatioMetricCanCalculateDeltas() {
    Metric<IntPercentage> delta = killRatio(undetected(3), totalMutations(7))
            .delta(killRatio(undetected(7), totalMutations(11)));
    assertThat(delta.getValue().getPercentage(), is((7f-3f)/(11f-7f) * 100f));
  }

  @Test
  public void mutatorMetricKnowsItsName() {
    MutatorMetric m = new MutatorMetric("NonVoidMethodCall");
    assertThat(m.getName(), is("NonVoidMethodCall"));
  }

  @Test
  public void metricKnowsItsType() {
    Metric metric = undetected(3);
    System.out.println(metric.getType());
  }

  private IntPercentMetric killRatio(Metric<Integer> numerator, Metric<Integer> denominator) {
    return MutationMetric.KILL_RATIO.createMetric(new IntPercentage(numerator, denominator));
  }

  private Metric<Integer> undetected(int undetected) {
    return MutationMetric.UNDETECTED.createMetric(undetected);
  }

  private Metric<Integer> totalMutations(int totalMutations) {
    return MutationMetric.TOTAL_MUTATIONS.createMetric(totalMutations);
  }
}
