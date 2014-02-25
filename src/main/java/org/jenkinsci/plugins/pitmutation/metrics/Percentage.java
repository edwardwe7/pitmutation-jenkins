package org.jenkinsci.plugins.pitmutation.metrics;

/**
 * @author Ed Kimber
 */
public abstract class Percentage<T> {
  public Percentage(Metric<T> numerator, Metric<T> denominator) {
    this.denominator_ = denominator;
    this.numerator_ = numerator;
  }

  public Metric<T> getNumerator() {
    return numerator_;
  }

  public Metric<T> getDenominator() {
    return denominator_;
  }

  public abstract float getPercentage();

  private Metric<T> denominator_;
  private Metric<T> numerator_;

}
