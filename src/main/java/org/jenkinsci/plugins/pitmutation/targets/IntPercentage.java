package org.jenkinsci.plugins.pitmutation.targets;

/**
 * @author Ed Kimber
 */
public class IntPercentage extends Percentage<Integer> {
  public IntPercentage(Metric<Integer> numerator, Metric<Integer> denominator) {
    super(numerator, denominator);
  }

  @Override
  public float getPercentage() {
    return (float)getNumerator().getValue() / (float)getDenominator().getValue() * 100f;
  }
}
