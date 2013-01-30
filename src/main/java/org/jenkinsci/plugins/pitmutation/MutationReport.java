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

  public MutationReport(PitBuildAction action, InputStream xmlReport) throws IOException, SAXException {
    action_ = action;
    digestMutations(xmlReport);
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
  }

  private PitBuildAction action_;
  private List<Mutation> mutations_ = new ArrayList<Mutation>();
}
