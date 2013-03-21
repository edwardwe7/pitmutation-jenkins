package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.jenkinsci.plugins.pitmutation.Mutation;

import javax.swing.plaf.multi.MultiScrollBarUI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

