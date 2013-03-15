package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

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
  private AbstractBuild owner_;


  public MutatedClass(AbstractBuild owner, String name, Collection<Mutation> mutations) {
    owner_ = owner;
    name_ = name;
    mutations_ = mutations;

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
    try {
      return new TextFile(new File(owner_.getRootDir(), "mutation-reports/" + package_ + File.pathSeparator + fileName_)).read();
    }
    catch (IOException exception) {
      return "Could not read file.";
    }
  }

  public Collection<Mutation> getMutations() {
    return mutations_;
  }

}
