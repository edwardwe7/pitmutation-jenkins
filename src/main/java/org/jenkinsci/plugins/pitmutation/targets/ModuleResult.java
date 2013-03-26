package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.*;
import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;

/**
 * @author edward
 */
public class ModuleResult extends MutationResult implements Serializable  {

  public ModuleResult(String name, MutationResult parent, MutationReport report) {
    super(name, parent);
    name_ = name;
    report_ = report;
  }

  public String getDisplayName() {
    return "Module: " + getName();
  }

  @Override
  public MutationStats getMutationStats() {
    return report_.getMutationStats();
  }

  public Map<String, MutatedPackage> getChildMap() {
    return Maps.transformEntries(report_.getMutationsByPackage().asMap(), packageTransformer_);
  }

//  public Collection<MutationStats> getStatsForNewTargets() {
//    return Maps.transformEntries(
//            Maps.difference(
//                    reports_.getFirst().getMutationsByClass().asMap(),
//                    reports_.getSecond().getMutationsByClass().asMap())
//                    .entriesOnlyOnLeft(),
//            statsTransformer_).values();
//  }

//  public Collection<Pair<MutatedClass>> getClassesWithNewSurvivors() {
//    return Maps.transformEntries(mutationDifference_, classMutationDifferenceTransform_).values();
//  }

  public String getName() {
    return name_;
  }

  private Maps.EntryTransformer<String, Collection<Mutation>, MutatedPackage> packageTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutatedPackage>() {
            public MutatedPackage transformEntry(String name, Collection<Mutation> mutations) {
              logger.log(Level.FINER, "found " + report_.getMutationsForPackage(name).size() + " reports for " + name);
              return new MutatedPackage(name, ModuleResult.this, Multimaps.index(report_.getMutationsForPackage(name), MutationReport.classIndexFunction));
            }
          };


  private static final Maps.EntryTransformer<String, Collection<Mutation>, MutationStats> statsTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutationStats>() {
            public MutationStats transformEntry(String name, Collection<Mutation> mutations) {
              return new MutationStatsImpl(name, mutations);
            }
          };


//  private Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, Pair<MutatedClass>> classMutationDifferenceTransform_ =
//          new Maps.EntryTransformer<String, MapDifference.ValueDifference<Collection<Mutation>>, Pair<MutatedClass>>() {
//            public Pair<MutatedClass> transformEntry(String name, MapDifference.ValueDifference<Collection<Mutation>> value) {
////              return MutatedClass.createPair(name, getOwner(), value.leftValue(), value.rightValue());
//            }
//          };

  private static final Logger logger = Logger.getLogger(ModuleResult.class.getName());

  private Map<String,MapDifference.ValueDifference<Collection<Mutation>>> mutationDifference_;
  private MutationReport report_;
  private String name_;
}
