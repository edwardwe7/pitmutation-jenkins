package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.utils.Pair;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * User: Ed Kimber
 */
public abstract class MutationResult<C extends MutationResult> {

  public MutationResult(AbstractBuild owner, Pair<MutationStats> stats) {
    owner_ = owner;
    stats_ = stats;
  }

  public abstract String getName();

  public abstract String getDisplayName();

  public abstract Map<String, C> getChildMap();

  public boolean isSourceLevel() {
    return false;
  }

  public boolean isCoverageAltered() {
    return false;
  }

  public Collection<C> getChildren() {
    return Ordering.natural().onResultOf(getUndetectedDeltaFunction).reverse().sortedCopy(getChildMap().values());
  }

  public MutationStats getMutationStats() {
    return stats_.getFirst();
  }

  public MutationStats getStatsDelta() {
    return stats_.getFirst().delta(stats_.getSecond());
  }

  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
    for (String name : getChildMap().keySet()) {
      if (urlTransform(name).equalsIgnoreCase(token)) {
        return getChildMap().get(name);
      }
    }
    return "#";
  }

  public MutationResult getChild(String name) {
    return getChildMap().get(name.toLowerCase());
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  public String getUrl() {
    return urlTransform(getName());
  }

  String urlTransform(String token) {
    StringBuilder buf = new StringBuilder(token.length());
    for (int i = 0; i < token.length(); i++) {
      final char c = token.charAt(i);
      if (('0' <= c && '9' >= c)
              || ('A' <= c && 'Z' >= c)
              || ('a' <= c && 'z' >= c)) {
        buf.append(c);
      } else {
        buf.append('_');
      }
    }
    return buf.toString();
  }

  private Function<MutationResult, Integer> getUndetectedDeltaFunction =
          new Function<MutationResult, Integer>() {
    public Integer apply(MutationResult result) {
      return result.getStatsDelta().getUndetected();
    }
  };

  private AbstractBuild owner_;
  private Pair<MutationStats> stats_;
}
