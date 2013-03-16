package org.jenkinsci.plugins.pitmutation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author edward
 */
public class MutationReportTest {

  @Before
  public void setUp() {
    mutationsXml_ = new InputStream[2];
    mutationsXml_[0] = getClass().getResourceAsStream("mutations-00.xml");
  }

  @Test
  public void countsKills() throws IOException, SAXException {
    MutationReport report = new MutationReport(mutationsXml_[0]);
    assertThat(report.getKillRatio(), is(new Ratio(5,16)));
  }

  @Test
  public void sortsMutationsByClassName() throws IOException, SAXException {
    MutationReport report = new MutationReport(mutationsXml_[0]);
    Collection<Mutation> mutations = report.getMutationsForClassName("org.jenkinsci.plugins.pitmutation.MutationReport");
    assertThat(mutations.size(), is(5));
  }

  @Test
  public void canDigestAMutation() throws IOException, SAXException {
    MutationReport report = new MutationReport(new ByteArrayInputStream(MUTATIONS.getBytes()));

    assertThat(report.getMutationStats().getTotalMutations(), is(2));

    Iterator<Mutation> mutations =
            report.getMutationsByClass().get("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile").iterator();

    Mutation m2 = mutations.next();
    Mutation m1 = mutations.next();

    assertThat(m1.isDetected(), is(true));
    assertThat(m1.getLineNumber(), is(54));
    assertThat(m1.getStatus(), is("NO_COVERAGE"));
    assertThat(m1.getSourceFile(), is("SafeMultipartFile.java"));
    assertThat(m1.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
    assertThat(m1.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));

    assertThat(m2.isDetected(), is(false));
    assertThat(m2.getLineNumber(), is(57));
    assertThat(m2.getStatus(), is("KILLED"));
    assertThat(m2.getSourceFile(), is("SafeMultipartFile.java"));
    assertThat(m2.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
    assertThat(m2.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));
  }

  private InputStream[] mutationsXml_;

  private final String MUTATIONS =
          "<mutations>"
        + "<mutation detected='true' status='NO_COVERAGE'>\n"
        + "<sourceFile>SafeMultipartFile.java</sourceFile>\n"
        + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>\n"
        + "<mutatedMethod>getSize</mutatedMethod>\n"
        + "<lineNumber>54</lineNumber>\n"
        + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>\n"
        + "<index>5</index>\n"
        + "<killingTest/>\n"
        + "</mutation>"
        + "<mutation detected='false' status='KILLED'>"
        + "<sourceFile>SafeMultipartFile.java</sourceFile>"
        + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>"
        + "<mutatedMethod>getSize</mutatedMethod>"
        + "<lineNumber>57</lineNumber>"
        + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>"
        + "<index>6</index>"
        + "<killingTest/>"
        + "</mutation>"
        + "</mutations>";
}
