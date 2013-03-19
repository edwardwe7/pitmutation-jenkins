package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.jenkinsci.plugins.pitmutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.model.AbstractBuild;
import hudson.util.TextFile;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.utils.Pair;

/**
 * User: Ed Kimber
 */
public class MutatedClass extends MutationResult {

  public MutatedClass(String name, MutationResult parent, Collection<Mutation> mutations) {
    super(name, parent);
    name_ = name;
    mutations_ = mutations;

    int lastDot = name.lastIndexOf('.');
    int firstDollar = name.indexOf('$');
    package_ = lastDot >= 0 ? name.substring(0, lastDot) : "";
    fileName_ = firstDollar >= 0
            ? lastDot >= 0 ? name.substring(lastDot + 1, firstDollar) + ".java.html" : ""
            : lastDot >= 0 ? name.substring(lastDot + 1) + ".java.html" : "";

    mutatedLines_ = createMutatedLines(mutations);
  }

  private Map<String, MutatedLine> createMutatedLines(Collection<Mutation> mutations) {
    HashMultimap<String, Mutation> multimap = HashMultimap.create();
    for (Mutation m : mutations) {
      multimap.put(String.valueOf(m.getLineNumber()), m);
    }
    return Maps.transformEntries(multimap.asMap(), lineTransformer_);
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

  private void setLineSourceLinks() {
    String source = getSourceFileContent();
    for (String line : mutatedLines_.keySet()) {
      Pattern p = Pattern.compile("(#.*_"+line+")\\'");
      Matcher m = p.matcher(source);
      if (m.find()) {
        logger_.log(Level.WARNING, "(0) " + m.group(0) + "   (1) " + m.group(1));
        mutatedLines_.get(line).setUrl(m.group(1));
      }
    }
  }

  public String getDisplayName() {
    return "Class: " + getName();
  }

  @Override
  public MutationStats getMutationStats() {
    return new MutationStatsImpl(getName(), mutations_);
  }

  public Map<String, MutatedLine> getChildMap() {
    return mutatedLines_;
  }

  private final Maps.EntryTransformer<String, Collection<Mutation>, MutatedLine> lineTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutatedLine>() {
            public MutatedLine transformEntry(String line, Collection<Mutation> mutations) {
              return new MutatedLine(line, MutatedClass.this, mutations);
            }
          };

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
  private Collection<Mutation> mutations_;
  private Map<String, MutatedLine> mutatedLines_;
}
