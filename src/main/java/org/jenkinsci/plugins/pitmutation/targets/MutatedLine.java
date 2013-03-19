package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author edward
 */
public class MutatedLine extends MutationResult implements Comparable {

  public MutatedLine(String line, MutationResult parent, Collection<Mutation> mutations) {
    super(line, parent);
    mutations_ = mutations;
    lineNumber_ = Integer.valueOf(line);
  }

  public Collection<Mutation> getMutations() {
    return mutations_;
  }

  public int getMutationCount() {
    return mutations_.size();
  }

  public int compareTo(Object o) {
    return ((MutatedLine) o).lineNumber_ - lineNumber_;
  }

  @Override
  public String getName() {
    return String.valueOf(lineNumber_);
  }

  @Override
  public String getDisplayName() {
    return getName();
  }

  @Override
  public MutationStats getMutationStats() {
    return new MutationStatsImpl(getName(), mutations_);
  }

  @Override
  public Map<String, MutationResult> getChildMap() {
    return new HashMap<String, MutationResult>();
  }

  public String getChildUrl() {
    logger_.log(Level.WARNING, "getChildUrl: " + url_);
    return url_;
  }

  public void setUrl(String url) {
    url_ = url;
    logger_.log(Level.WARNING, "setUrl: " + url_);

  }

  private int lineNumber_;
  private Collection<Mutation> mutations_;
  private String url_;
}
