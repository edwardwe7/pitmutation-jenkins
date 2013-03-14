package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;
import java.util.*;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

/**
 * @author edward
 */
public class MutationResult implements Serializable {
  public MutationResult(PitBuildAction action) {
    action_ = action;
  }

  public AbstractBuild getOwner() {
    return action_.getOwner();
  }

  public MutationStats getMutationStats() {
    return new MutationStats();
  }

  public Collection<Mutation> findDifferentMutations(String className) {
    MutationReport report = action_.getReport();
    MutationReport previous = action_.getPreviousAction().getReport();

    Set<Mutation> mutations = new HashSet<Mutation>(report.getMutationsForClassName(className));
    Set<Mutation> previousMutations = new HashSet<Mutation>(previous.getMutationsForClassName(className));

    mutations.removeAll(previousMutations);
    return mutations;
  }

  public Collection<Mutation> findNewSurvivors(String className) {
    Collection<Mutation> survivors = new ArrayList<Mutation>();

    for (Mutation m : findDifferentMutations(className)) {
      if (!m.isDetected()) {
        survivors.add(m);
      }
    }

    return survivors;
  }

  private PitBuildAction action_;
}
