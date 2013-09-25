package org.jenkinsci.plugins.pitmutation.target;

import org.jenkinsci.plugins.pitmutation.metrics.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ed Kimber
 */
public class MutationSetTest {

  @Test
  public void basicOps() {
    MetricSet metricSet = new MetricSet();
    Metric<Integer> undetected = undetected(8);
    Metric<Integer> total = totalMutations(16);

    metricSet.put(MutationMetric.UNDETECTED, undetected);
    metricSet.put(MutationMetric.TOTAL_MUTATIONS, total);
    metricSet.put(MutationMetric.KILL_RATIO, killRatio(undetected, total));

    assertThat(metricSet.getMetric(MutationMetric.UNDETECTED).getValue(), is(8));
    assertThat(metricSet.getMetric(MutationMetric.TOTAL_MUTATIONS).getValue(), is(16));
    assertThat(metricSet.getMetric(MutationMetric.KILL_RATIO).getValue().getPercentage(), is(50f));
  }

  @Test
  public void cannotMismatchMutationTypes() {
    MetricSet metricSet = new MetricSet();
    //the point of this "test" is that the following line does not compile
//    metricSet.put(MutationMetric.UNDETECTED, killRatio(undetected(1), totalMutations(4)));
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
