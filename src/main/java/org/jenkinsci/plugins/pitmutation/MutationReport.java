package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import net.sf.json.filters.MappingPropertyFilter;
import org.apache.commons.digester3.Digester;
import org.jenkinsci.plugins.pitmutation.targets.MutatedLine;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.jenkinsci.plugins.pitmutation.targets.MutationStatsImpl;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class MutationReport {

  public MutationReport(InputStream xmlReport) throws IOException, SAXException {
    this();
    digestMutations(xmlReport);
  }

  private MutationReport() {
    mutationsByClass_ = HashMultimap.create();
    lineMutationsByClass_ = HashMultimap.create();
  }

  public static MutationReport createEmptyReport() {
    return new MutationReport();
  }

  //---

  public Ratio getKillRatio() {
    return this.killRatio_;
  }

  public Multimap<String, Mutation> getMutationsByClass() {
    return mutationsByClass_;
  }

  public Collection<MutatedLine> getMutatedLinesForClass(String className) {
    return lineMutationsByClass_.get(className);
  }

  public Multimap<String, Mutation> getSurvivors() {
    return Multimaps.filterValues(mutationsByClass_, isSurvivor_);
  }

  public Collection<Mutation> getMutationsForClassName(String className) {
    Collection<Mutation> mutations = mutationsByClass_.get(className);
    return mutations != null ? mutations : EMPTY_SET;
  }

  private void digestMutations(InputStream input) throws IOException, SAXException {
    Digester digester = new Digester();
    digester.addObjectCreate("mutations/mutation", Mutation.class);
    digester.addSetNext("mutations/mutation", "add", "org.jenkinsci.plugins.pitmutation.Mutation");
    digester.addSetProperties("mutations/mutation");
    digester.addSetNestedProperties("mutations/mutation");
    ArrayList<Mutation> mutations = new ArrayList<Mutation>();
    digester.push(mutations);
    digester.parse(input);

    float killed = 0;
    float mutationCount = 0;
    for (Mutation mutation : mutations) {
      if (mutation.isDetected()) {
          killed++;
      }
      mutationCount++;
      mutationsByClass_.put(mutation.getMutatedClass(), mutation);
    }
    killRatio_ = new Ratio(killed, mutationCount);
  }

  public MutationStats getMutationStats() {
    return new MutationStats() {
      public String getTitle() {
        return "ALL";
      }

      public int getUndetected() {
        return (int) killRatio_.getDenominator() - (int) killRatio_.getNumerator();
      }

      public int getTotalMutations() {
        return (int) killRatio_.getDenominator();
      }
    };
  }

  public static Predicate<Mutation> isSurvivor_ = new Predicate<Mutation>() {
    public boolean apply(Mutation mutation) {
      return !mutation.isDetected();
    }
  };

  private static final Set<Mutation> EMPTY_SET = new HashSet<Mutation>();

  private Multimap<String, MutatedLine> lineMutationsByClass_;
  private Multimap<String, Mutation> mutationsByClass_;
  private Ratio killRatio_ = new Ratio(0,0);
}
