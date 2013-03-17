package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Ratio;

/**
 * User: Ed Kimber
 * Date: 15/03/13
 * Time: 21:22
 */
public abstract class MutationStats {
  public abstract String getTitle();

  public abstract int getUndetected();

  public abstract int getTotalMutations();

  public float getKillPercent() {
    return new Ratio(getUndetected(), getTotalMutations()).asPercentage();
  }

  public MutationStats delta(final MutationStats other) {
    return new MutationStats() {
      @Override
      public String getTitle() {
        return MutationStats.this.getTitle();
      }

      @Override
      public int getUndetected() {
        return MutationStats.this.getUndetected() - other.getUndetected();
      }

      @Override
      public int getTotalMutations() {
        return MutationStats.this.getTotalMutations() - other.getTotalMutations();
      }
    };
  }
}
