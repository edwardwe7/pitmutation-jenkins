package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import hudson.model.AbstractBuild;
import hudson.util.TextFile;

/**
 * User: Ed Kimber
 * Date: 15/03/13
 * Time: 02:31
 */
public class MutatedClass {
  private String name_;
  private String package_;
  private String fileName_;
  private Collection<Mutation> mutations_;
  private Map<Integer, Collection<Mutation>> lineMap_;
  private AbstractBuild owner_;

  public MutatedClass(AbstractBuild owner, String name, Collection<Mutation> mutations) {
    owner_ = owner;
    name_ = name;
    mutations_ = mutations;

    int lastDot = name.lastIndexOf('.');
    package_ = name_.substring(0, lastDot);
    fileName_ = name_.substring(lastDot + 1) + ".java.html";

    lineMap_ = new HashMap<Integer, Collection<Mutation>>();
    aggregateLines();
  }

  private void aggregateLines() {
    for (Mutation mutation : mutations_) {
      Collection<Mutation> lines = lineMap_.get(mutation.getLineNumber());
      if (lines == null) {
        lines = new ArrayList<Mutation>();
        lineMap_.put(mutation.getLineNumber(), lines);
      }
      lines.add(mutation);
    }
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

  public String getSourceFileContent() {
    //can't return inner class content
    if (fileName_.contains("$")) {
      return "See main class.";
    }
    try {
      return new TextFile(new File(owner_.getRootDir(), "mutation-reports/" + package_ + File.separator + fileName_)).read();
    }
    catch (IOException exception) {
      return "Could not read source file: " + owner_.getRootDir().getPath()
             + "/mutation-reports/" + package_ + File.separator + fileName_ + "\n";
    }
  }

  public Collection<Integer> getLineAggregations() {
    return lineMap_.keySet();
  }

  public Collection<Mutation> getMutationsForLine(int lineNumber) {
    return lineMap_.get(lineNumber);
  }

  public int getNumberOfMutationsOn(int lineNumber) {
    logger.warning("Found " + lineMap_.get(lineNumber).size() + " mutations on line " + lineNumber);
    return lineMap_.get(lineNumber).size();
  }
  public Collection<Mutation> getMutations() {
    return mutations_;
  }

  private static final Logger logger = Logger.getLogger(MutationResult.class.getName());

}
