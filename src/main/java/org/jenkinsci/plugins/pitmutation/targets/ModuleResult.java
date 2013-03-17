package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import com.google.common.collect.*;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.utils.Pair;

/**
 * @author edward
 */
public class ModuleResult extends MutationResult implements Serializable  {

  public ModuleResult(String name, AbstractBuild owner, Pair<MutationReport> reports) {
    super(owner, new Pair<MutationStats>(
            reports.getFirst().getMutationStats(),
            reports.getSecond().getMutationStats()));
    reports_ = reports;
    name_ = name;
    mutationDifference_ = Maps.difference(
            reports_.getFirst().getSurvivors().asMap(), reports_.getSecond().getSurvivors().asMap())
            .entriesDiffering();
  }

//  public Collection<Mutation> getMutationsForClass(String className) {
//    return report_.getMutationsForClassName(className);
//  }

  public Collection<MutationStats> getStatsForNewTargets() {
    return Maps.transformEntries(
            Maps.difference(
                    reports_.getFirst().getMutationsByClass().asMap(),
                    reports_.getSecond().getMutationsByClass().asMap())
                    .entriesOnlyOnLeft(),
            statsTransformer_).values();
  }

  public Collection<Pair<MutatedClass>> getClassesWithNewSurvivors() {
    return Maps.transformEntries(mutationDifference_, classMutationDifferenceTransform_).values();
  }

  public String getName() {
    return name_;
  }

  private static final Maps.EntryTransformer<String, Collection<Mutation>, MutationStats> statsTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutationStats>() {
            public MutationStats transformEntry(String name, Collection<Mutation> mutations) {
              return new MutationStatsImpl(name, mutations);
            }
          };

  private Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, Pair<MutatedClass>> classMutationDifferenceTransform_ =
          new Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, Pair<MutatedClass>>() {
            public Pair<MutatedClass> transformEntry(String name, MapDifference.ValueDifference<Collection<Mutation>> value) {
              return new Pair<MutatedClass>(
                      MutatedClass.create(name, value.leftValue()),
                      MutatedClass.create(name, value.rightValue()));
            }
          };

  private static final Logger logger = Logger.getLogger(ModuleResult.class.getName());

  private Map<String,MapDifference.ValueDifference<Collection<Mutation>>> mutationDifference_;
  private Pair<MutationReport> reports_;
  private String name_;
}
