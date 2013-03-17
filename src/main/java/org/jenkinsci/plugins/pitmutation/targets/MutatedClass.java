package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import hudson.model.AbstractBuild;
import hudson.util.TextFile;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

/**
 * User: Ed Kimber
 * Date: 15/03/13
 * Time: 02:31
 */
public class MutatedClass extends MutationResult {

  public static MutatedClass create(String name, Collection<Mutation> mutations) {
    MutationStats stats = new MutationStatsImpl(name, mutations);
    MutatedClass product = new MutatedClass(name, stats);
    product.mutatedLines_ = MutatedLine.createMutatedLines(mutations);
    return product;
  }

  private MutatedClass(String name, MutationStats stats) {
    super(stats);
    name_ = name;

    int lastDot = name.lastIndexOf('.');
    package_ = name_.substring(0, lastDot);
    fileName_ = name_.substring(lastDot + 1) + ".java.html";
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

  public Collection<MutatedLine> getMutatedLines() {
    return mutatedLines_;
  }

  private String name_;
  private String package_;
  private String fileName_;
  private Collection<MutatedLine> mutatedLines_;
}
