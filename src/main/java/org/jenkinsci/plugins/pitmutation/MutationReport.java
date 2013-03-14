package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class MutationReport {

  public MutationReport(InputStream xmlReport) throws IOException, SAXException {
    mutationsByClass_ = new HashMap<String, Set<Mutation>>();
    digestMutations(xmlReport);
  }

  public List<Mutation> getMutations() {
    return this.mutations_;
  }

  public Ratio getKillRatio() {
    return this.killRatio_;
  }

  public Set<String> sourceFilenames() {
    return mutationsByClass_.keySet();
  }

  public Set<Mutation> getMutationsForClassName(String className) {
    return mutationsByClass_.get(className);
  }

  private void digestMutations(InputStream input) throws IOException, SAXException {
    Digester digester = new Digester();
    digester.addObjectCreate("mutations/mutation", Mutation.class);
    digester.addSetNext("mutations/mutation", "add", "org.jenkinsci.plugins.pitmutation.Mutation");
    digester.addSetProperties("mutations/mutation");
    digester.addSetNestedProperties("mutations/mutation");
    mutations_ = new ArrayList<Mutation>();
    digester.push(mutations_);
    digester.parse(input);

    float killed = 0;
    float mutations = 0;
    for (Mutation mutation : mutations_) {
      if (mutation.isDetected()) {
          killed++;
      }
      mutations++;
      putMutationInMap(mutation);
    }
    killRatio_ = new Ratio(killed, mutations);
  }

  private void putMutationInMap(Mutation mutation) {
    String className = mutation.getMutatedClass();
    Set<Mutation> mutationSet = mutationsByClass_.get(className);
    if (mutationSet == null) {
      mutationSet = new HashSet<Mutation>();
      mutationsByClass_.put(className, mutationSet);
    }
    mutationSet.add(mutation);
  }

  private Map<String, Set<Mutation>> mutationsByClass_;
  private Ratio killRatio_;
  private List<Mutation> mutations_ = new ArrayList<Mutation>();
}
