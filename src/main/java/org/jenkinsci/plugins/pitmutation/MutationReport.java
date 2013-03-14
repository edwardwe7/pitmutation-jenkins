package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class MutationReport {

  public MutationReport(InputStream xmlReport) throws IOException, SAXException {
    digestMutations(xmlReport);
  }

  public List<Mutation> getMutations() {
    return this.mutations_;
  }

  public Ratio getKillRatio() {
    return this.killRatio_;
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
    }

    killRatio_ = new Ratio(mutations, killed);
  }

  private Ratio killRatio_;
  private List<Mutation> mutations_ = new ArrayList<Mutation>();
}
