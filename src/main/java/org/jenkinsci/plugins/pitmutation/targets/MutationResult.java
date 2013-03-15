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
    owner_ = action.getOwner();
    report_ = action.getReport();
    previous_ = action.getPreviousAction().getReport();
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  public Collection<Mutation> getMutationsForClass(String className) {
    return report_.getMutationsForClassName(className);
  }

  public Collection<String> findNewTargets() {
    Set<String> targets = new HashSet<String>(report_.sourceFilenames());
    targets.removeAll(previous_.sourceFilenames());
    return targets;
  }

  public Collection<MutationStats> getStatsForNewTargets() {
    ArrayList<MutationStats> stats = new ArrayList<MutationStats>();
    for (String className : findNewTargets()) {
      stats.add(new MutationStats(className, report_.getMutationsForClassName(className)));
    }
    return stats;
  }


  public Collection<Mutation> getDifferentMutations(String className) {
    Set<Mutation> mutations = new HashSet<Mutation>(report_.getMutationsForClassName(className));
    mutations.removeAll(previous_.getMutationsForClassName(className));
    return mutations;
  }

  public Collection<Mutation> getNewSurvivors(String className) {
    Collection<Mutation> survivors = new ArrayList<Mutation>();

    for (Mutation m : getDifferentMutations(className)) {
      if (!m.isDetected()) {
        survivors.add(m);
      }
    }

    return survivors;
  }

  private MutationReport report_;
  private MutationReport previous_;
  private AbstractBuild owner_;
}
