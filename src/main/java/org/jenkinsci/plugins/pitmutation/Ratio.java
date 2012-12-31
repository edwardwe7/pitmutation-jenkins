package org.jenkinsci.plugins.pitmutation;

/**
 * @author edward
 */
public class Ratio implements  Comparable<Ratio> {
  public Ratio(float numerator, float denominator) {
    numerator_ = numerator;
    denominator_ = denominator;
  }

  public float getNumerator() {
    return numerator_;
  }

  public float getDenominator() {
    return denominator_;
  }

  public float asPercentage() {
    if (denominator_ == 0) {
      return 100;
    }
    else {
      return numerator_ / denominator_;
    }
  }

  public int compareTo(final Ratio other) {
    if (this.denominator_ == 0) {
      if (other.denominator_ == 0) {
        return Float.compare(this.numerator_, other.numerator_);
      }
      else {
        return 1;
      }
    }
    else {
      if (other.denominator_ == 0) {
        return -1;
      }
      else {
        return Float.compare(this.numerator_ / this.denominator_, other.numerator_ / other.denominator_);
      }
    }
  }

  /**
   * Gets "x/y" representation.
   */
  public String toString() {
    return print(numerator_)+"/"+print(denominator_);
  }

  private String print(float f) {
    int i = (int) f;
    if(i==f)
      return String.valueOf(i);
    else
      return String.valueOf(f);
  }

  private float numerator_ = 0;
  private float denominator_ = 0;
}
