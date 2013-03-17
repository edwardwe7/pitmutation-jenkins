package org.jenkinsci.plugins.pitmutation.targets;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.utils.Pair;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Ed Kimber
 */
public class MutationResult {

  public MutationResult(AbstractBuild owner, Pair<MutationStats> stats) {
    owner_ = owner;
    stats_ = stats;
    children_ = new HashMap<String, MutationResult>();
  }

  public MutationStats getMutationStats() {
    return stats_.getFirst();
  }

  public MutationStats getStatsDelta() {
    return stats_.getFirst().delta(stats_.getSecond());
  }

  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
    return getChild(token);
  }

  public MutationResult getChild(String name) {
    return children_.get(name.toLowerCase());
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  private AbstractBuild owner_;
  private Pair<MutationStats> stats_;
  private Map<String, MutationResult> children_;
}
