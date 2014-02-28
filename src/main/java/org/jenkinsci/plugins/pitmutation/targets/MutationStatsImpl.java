package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;

/**
 * @author  Ed Kimber
 */
public class MutationStatsImpl extends MutationStats {
  public MutationStatsImpl(String title, Collection<Mutation> mutations) {
    title_ = title;
    mutationsByType_ = HashMultiset.create();
    if (mutations == null) return;
    for (Mutation m : mutations) {
      if (!m.isDetected()) {
        undetected_++;
      }
      mutationsByType_.add(m.getMutatorClass());
    }

    total_ = mutations.size();
  }

  public String getTitle() {
    return title_;
  }

  public int getUndetected() {
    return undetected_;
  }

  public int getTotalMutations() {
    return total_;
  }

  private String title_;
  private int undetected_ = 0;
  private int total_;
  private Multiset<String> mutationsByType_;
}

