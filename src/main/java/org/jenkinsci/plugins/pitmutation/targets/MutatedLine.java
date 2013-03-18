package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.*;
import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author edward
 */
public class MutatedLine extends MutationResult implements Comparable {

  public static Map<String, MutatedLine> createMutatedLines(
          Collection<Mutation> mutations, Collection<Mutation> previousMutations)
  {
    Multimap<String, Mutation> multimap = TreeMultimap.create(Ordering.natural(), Ordering.arbitrary());
    for (Mutation m : mutations) {
      multimap.put(String.valueOf(m.getLineNumber()), m);
    }
    return Maps.transformEntries(multimap.asMap(), lineTransformer_);
  }

  public MutatedLine(int lineNumber, Collection<Mutation> mutations) {
    super(null, null);
    lineNumber_ = lineNumber;
    mutations_ = mutations;
  }

  public int getLineNumber() {
    return lineNumber_;
  }

  public Collection<Mutation> getMutations() {
    return mutations_;
  }

  public int getMutationCount() {
    return mutations_.size();
  }

  private int lineNumber_;
  private Collection<Mutation> mutations_;

  private static final Maps.EntryTransformer<String, Collection<Mutation>, MutatedLine> lineTransformer_ =
          new Maps.EntryTransformer<String, Collection<Mutation>, MutatedLine>() {
            public MutatedLine transformEntry(String line, Collection<Mutation> mutations) {
              return new MutatedLine(Integer.parseInt(line), mutations);
            }
          };

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
  public Map<String, ? extends MutationResult> getChildMap() {
    return new HashMap<String, MutationResult>();
  }
}
