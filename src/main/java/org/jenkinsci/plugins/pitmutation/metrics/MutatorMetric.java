package org.jenkinsci.plugins.pitmutation.metrics;

/**
 * @author Ed Kimber
 */
public class MutatorMetric extends MutationMetric<IntMetric, Integer> {

//  public MutatorMetric(Integer value) {
//    super(value);
//  }
//
//  public Metric aggregate(Metric metric) {
//    return null;  //To change body of implemented methods use File | Settings | File Templates.
//  }
//
//  public Metric delta(Metric metric) {
//    return null;  //To change body of implemented methods use File | Settings | File Templates.
//  }
//
//  private Multiset mutatorSet

  public MutatorMetric(String name) {
    super(name, IntMetric.class);
  }
}
