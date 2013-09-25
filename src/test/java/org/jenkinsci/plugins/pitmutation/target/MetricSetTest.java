package org.jenkinsci.plugins.pitmutation.target;

import org.jenkinsci.plugins.pitmutation.metrics.MetricSet;
import org.jenkinsci.plugins.pitmutation.metrics.MutationMetric;
import org.jenkinsci.plugins.pitmutation.metrics.MutatorMetric;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * @author Ed Kimber
 */
public class MetricSetTest {
  @Test
  public void canAddMutationToSet(){
    MetricSet set = new MetricSet();
    set.put(MutationMetric.TOTAL_MUTATIONS, MutationMetric.TOTAL_MUTATIONS.createMetric(12));
    assertThat(set.getMetricTypes(), hasItem(MutationMetric.TOTAL_MUTATIONS));
    assertThat(set.getMetric(MutationMetric.TOTAL_MUTATIONS).getValue(), is(12));
  }

}
