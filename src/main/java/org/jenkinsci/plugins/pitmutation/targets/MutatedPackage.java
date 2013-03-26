package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Ed Kimber
 */
public class MutatedPackage extends MutationResult {

  public MutatedPackage(String name, MutationResult parent, Multimap<String, Mutation> classMutations) {
    super(name, parent);
    classMutations_ = classMutations;
  }

  @Override
  public String getDisplayName() {
    return "Package: " + getName();
  }

  @Override
  public MutationStats getMutationStats() {
    return new MutationStatsImpl(getName(), classMutations_.values());
  }

  @Override
  public Map<String, ? extends MutationResult> getChildMap() {
    return Maps.transformEntries(classMutations_.asMap(), classTransformer_);
  }

  private Maps.EntryTransformer<String, Collection<Mutation>, MutatedClass> classTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutatedClass>() {
            public MutatedClass transformEntry(String name, Collection<Mutation> mutations) {
              logger_.log(Level.FINER, "found " + mutations.size() + " reports for " + name);
              return new MutatedClass(name, MutatedPackage.this, mutations);
            }
          };


  private Multimap<String, Mutation> classMutations_;
}
