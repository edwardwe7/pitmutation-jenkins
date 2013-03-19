package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.*;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.utils.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author edward
 */
public class MutatedLine extends MutationResult implements Comparable {

  private static Multimap<String, Mutation> multimapPrevious_;
  private static AbstractBuild owner_;
  public static Map<String, MutatedLine> createMutatedLines(AbstractBuild owner,
          Collection<Mutation> mutations, Collection<Mutation> previousMutations)
  {
    owner_ = owner;
    Multimap<String, Mutation> multimapLines = createMultimap(mutations);
    multimapPrevious_ = createMultimap(previousMutations);
    return Maps.transformEntries(multimapLines.asMap(), lineTransformer_);
  }

  private static Multimap<String, Mutation> createMultimap(Collection<Mutation> mutations) {
    TreeMultimap<String, Mutation> multimap = TreeMultimap.create(Ordering.natural(), Ordering.arbitrary());
    for (Mutation m : mutations) {
      multimap.put(String.valueOf(m.getLineNumber()), m);
    }
    return multimap;
  }

  public MutatedLine(int lineNumber, Collection<Mutation> mutations, Collection<Mutation> previousMutations) {
    super(owner_, new Pair<MutationStats>(
            new MutationStatsImpl("line", mutations), new MutationStatsImpl("line", previousMutations)));
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
              return new MutatedLine(Integer.parseInt(line), mutations, multimapPrevious_.asMap().get(line));
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
  public Map<String, Mutation> getChildMap() {
    return new HashMap<String, Mutation>();
  }
}
