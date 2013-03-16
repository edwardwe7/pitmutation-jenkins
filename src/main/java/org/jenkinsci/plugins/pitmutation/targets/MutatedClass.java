package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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


  public MutatedClass(AbstractBuild owner, String name, Collection<Mutation> mutations) {
    owner_ = owner;
    name_ = name;
    mutatedLines_ = MutatedLine.createMutatedLines(mutations);

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

  public Collection<MutatedLine> getMutatedLines() {
    return mutatedLines_;
  }

  private String name_;
  private String package_;
  private String fileName_;
  private Collection<MutatedLine> mutatedLines_;
  private AbstractBuild owner_;
}
