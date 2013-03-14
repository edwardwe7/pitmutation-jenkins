package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;

/**
 * User: Ed Kimber
 * Date: 14/03/13
 * Time: 21:49
 */
public class MutationStats {
  public MutationStats(Collection<Mutation> mutations) {
    for (Mutation m : mutations) {
      if (!m.isDetected()) {
        undetected_++;
      }
    }
  }

  public int countUndetected() {
    return undetected_;
  }

  private int undetected_ = 0;
}

