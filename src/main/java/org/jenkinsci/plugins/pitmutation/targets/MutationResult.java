package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.*;
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
    return report_.getMutationStats();
  }

  public MutationStats getStatsDelta() {
    return report_.getMutationStats().delta(previous_.getMutationStats());
  }

  public Collection<Mutation> getMutationsForClass(String className) {
    return report_.getMutationsForClassName(className);
  }

  public Collection<MutationStats> getStatsForNewTargets() {
    return Maps.transformEntries(
            Maps.difference(
                    report_.getMutationsByClass().asMap(),
                    previous_.getMutationsByClass().asMap())
                    .entriesOnlyOnLeft(),
            statsTransformer_).values();
  }

  public Collection<MutatedClass> getClassesWithNewSurvivors() {
    Map<String,MapDifference.ValueDifference<Collection<Mutation>>> difference =
            Maps.difference(
                    report_.getSurvivors().asMap(),
                    previous_.getSurvivors().asMap())
                    .entriesDiffering();

    return Maps.transformEntries(difference, classMutationDifferenceTransform_).values();
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  private static final Maps.EntryTransformer<String, Collection<Mutation>, MutationStats> statsTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutationStats>() {
            public MutationStats transformEntry(String name, Collection<Mutation> mutations) {
              return new MutationStatsImpl(name, mutations);
            }
          };

  private Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, MutatedClass> classMutationDifferenceTransform_ =
          new Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, MutatedClass>() {
            public MutatedClass transformEntry(String name, MapDifference.ValueDifference<Collection<Mutation>> value) {
              Collection<Mutation> newMutations = Lists.newArrayList(value.leftValue());
              newMutations.removeAll(value.rightValue());
              return new MutatedClass(owner_, name, newMutations);
            }
          };

  private static final Logger logger = Logger.getLogger(MutationResult.class.getName());

  private MutationReport report_;
  private MutationReport previous_;
  private AbstractBuild owner_;
}
