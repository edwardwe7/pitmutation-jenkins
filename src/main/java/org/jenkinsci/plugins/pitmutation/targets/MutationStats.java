package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;

/**
 * User: Ed Kimber
 * Date: 14/03/13
 * Time: 21:49
 */
public class MutationStats {
  public MutationStats(String title, Collection<Mutation> mutations) {
    title_ = title;
    for (Mutation m : mutations) {
      if (!m.isDetected()) {
        undetected_++;
      }
    }
  }

  public String getTitle() {
    return title_;
  }

  public int getUndetected() {
    return undetected_;
  }

  private String title_;
  private int undetected_ = 0;
}

