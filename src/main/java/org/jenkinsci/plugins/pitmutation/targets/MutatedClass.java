package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.jenkinsci.plugins.pitmutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hudson.model.AbstractBuild;
import hudson.util.TextFile;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.utils.Pair;

/**
 * User: Ed Kimber
 */
public class MutatedClass extends MutationResult {

  public static Pair<MutatedClass> createPair(String name, AbstractBuild owner,
                                              Collection<Mutation> mutations,
                                              Collection<Mutation> previousMutations)
  {
    Pair<MutationStats> stats = new Pair<MutationStats>(
            new MutationStatsImpl(name, mutations),
            new MutationStatsImpl(name, previousMutations));
    MutatedClass product = new MutatedClass(name, owner, stats);
    product.mutatedLines_ = MutatedLine.createMutatedLines(owner, mutations, previousMutations);
    return new Pair<MutatedClass>(product, new MutatedClass(name, owner, null));
  }

  public static MutatedClass create(String name, AbstractBuild owner,
                                    Collection<Mutation> mutations,
                                    Collection<Mutation> previousMutations) {
    Pair<MutationStats> stats = new Pair<MutationStats>(
            new MutationStatsImpl(name, mutations),
            new MutationStatsImpl(name, previousMutations));
    MutatedClass product = new MutatedClass(name, owner, stats);
    product.mutatedLines_ = MutatedLine.createMutatedLines(owner, mutations, previousMutations);
    return product;
  }

  private MutatedClass(String name, AbstractBuild owner, Pair<MutationStats> stats) {
    super(owner, stats);
    name_ = name;

    int lastDot = name.lastIndexOf('.');
    int firstDollar = name.indexOf('$');
    package_ = lastDot >= 0 ? name.substring(0, lastDot) : "";
    fileName_ = firstDollar >= 0
            ? lastDot >= 0 ? name.substring(lastDot + 1, firstDollar) + ".java.html" : ""
            : lastDot >= 0 ? name.substring(lastDot + 1) + ".java.html" : "";
  }

  @Override
  public boolean isSourceLevel() {
    return true;
  }

  public String getSourceFileContent() {
    try {
      return new TextFile(new File(getOwner().getRootDir(), "mutation-report/" + package_ + File.separator + fileName_)).read();
    }
    catch (IOException exception) {
      return "Could not read source file: " + getOwner().getRootDir().getPath()
              + "/mutation-report/" + package_ + File.separator + fileName_ + "\n";
    }
  }

  public String getDisplayName() {
    return "Class: " + getName();
  }

  public Map<String, MutatedLine> getChildMap() {
    return mutatedLines_;
  }

  public Collection<MutatedLine> getChildren() {
    return Ordering.natural().reverse().sortedCopy(mutatedLines_.values());
  }

  public String getName() {
    return name_;
  }

  public String getFileName() {
    return fileName_;
  }

  public String getPackage() {
    return package_;
  }

  private String name_;
  private String package_;
  private String fileName_;
  private Map<String, MutatedLine> mutatedLines_;
}
