package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.jenkinsci.plugins.pitmutation.Mutation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author edward
 */
public class MutatedLine extends MutationResult<MutatedLine> {

  public MutatedLine(String line, MutationResult parent, Collection<Mutation> mutations) {
    super(line, parent);
    mutations_ = mutations;
    lineNumber_ = Integer.valueOf(line);
  }

  public Collection<String> getMutators() {
    return new HashSet<String>(Collections2.transform(mutations_, getMutatorClasses_));
  }
//
//  public int getMutationCount() {
//    return mutations_.size();
//  }

  public int compareTo(MutatedLine other) {
    return other.lineNumber_ - lineNumber_;
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
  public Map<String, MutationResult<?>> getChildMap() {
    return new HashMap<String, MutationResult<?>>();
  }

  public String getUrl() {
    String source = getParent().getSourceFileContent();
    Pattern p = Pattern.compile("(#org.*_" + getName() + ")\\'");
    Matcher m = p.matcher(source);
    if (m.find()) {
      return m.group(1);
    }
    return super.getUrl();
  }

  private static final Function<Mutation, String> getMutatorClasses_ = new Function<Mutation, String>() {
    public String apply(Mutation mutation) {
      return mutation.getMutatorClass();
    }
  };

  private int lineNumber_;
  private Collection<Mutation> mutations_;
}
