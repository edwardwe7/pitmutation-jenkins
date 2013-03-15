package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  public MutationStats getOverallStats() {
    return new MutationStats("ALL", report_.getMutations());
  }

  public Collection<Mutation> getMutationsForClass(String className) {
    return report_.getMutationsForClassName(className);
  }

  public Collection<String> findNewTargets() {
    Set<String> targets = new HashSet<String>(report_.sourceClasses());
    targets.removeAll(previous_.sourceClasses());
    return targets;
  }

  public Collection<MutationStats> getStatsForNewTargets() {
    ArrayList<MutationStats> stats = new ArrayList<MutationStats>();
    for (String className : findNewTargets()) {
      stats.add(new MutationStats(className, report_.getMutationsForClassName(className)));
    }
    return stats;
  }

  public Collection<MutatedClass> getClassesWithNewSurvivors() {
    ArrayList<MutatedClass> classes = new ArrayList<MutatedClass>();

    for (String className : report_.sourceClasses()) {
      Collection<Mutation> mutations = getNewSurvivors(className);
      if (mutations.size() > 0) {
        classes.add(new MutatedClass(owner_, className, mutations));
      }
    }
    return classes;
  }

  public Collection<Mutation> getDifferentMutations(String className) {
    Set<Mutation> mutations = new HashSet<Mutation>(report_.getMutationsForClassName(className));
    mutations.removeAll(previous_.getMutationsForClassName(className));
    return mutations;
  }

  public Collection<Mutation> getNewSurvivors(String className) {
    ArrayList<Mutation> survivors = new ArrayList<Mutation>();

    for (Mutation m : getDifferentMutations(className)) {
      if (!m.isDetected()) {
        survivors.add(m);
      }
    }

    logger.log(Level.WARNING, "Found " + survivors.size() + " in " + className);

    return survivors;
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  private static final Logger logger = Logger.getLogger(MutationResult.class.getName());

  private MutationReport report_;
  private MutationReport previous_;
  private AbstractBuild owner_;
}
