package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.apache.commons.digester3.Digester;
import org.jenkinsci.plugins.pitmutation.targets.MutatedLine;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class MutationReport {

  public MutationReport() {
    mutationsByClass_ = HashMultimap.create();
    lineMutationsByClass_ = HashMultimap.create();
  }

  public static MutationReport create(InputStream xmlReport) throws IOException, SAXException {
    return digestMutations(xmlReport);
  }

  private static MutationReport digestMutations(InputStream input) throws IOException, SAXException {
    Digester digester = new Digester();
    digester.addObjectCreate("mutations", MutationReport.class);
    digester.addObjectCreate("mutations/mutation", Mutation.class);
    digester.addSetNext("mutations/mutation", "addMutation", "org.jenkinsci.plugins.pitmutation.Mutation");
    digester.addSetProperties("mutations/mutation");
    digester.addSetNestedProperties("mutations/mutation");

    return digester.parse(input);
  }

  public void addMutation(Mutation mutation) {
    mutationsByClass_.put(mutation.getMutatedClass(), mutation);
    if (mutation.isDetected()) {
      killCount_++;
    }
  }

  //---

  public Ratio getKillRatio() {
    return new Ratio(killCount_, mutationsByClass_.values().size());
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



  public MutationStats getMutationStats() {
    return new MutationStats() {
      public String getTitle() {
        return "ALL";
      }

      public int getUndetected() {
        return (int) getKillRatio().getDenominator() - (int) getKillRatio().getNumerator();
      }

      public int getTotalMutations() {
        return (int) getKillRatio().getDenominator();
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
  private int killCount_ = 0;
}
