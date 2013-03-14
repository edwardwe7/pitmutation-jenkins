package org.jenkinsci.plugins.pitmutation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author edward
 */
public class MutationReportTest {

  @Test
  public void countsKills() throws IOException, SAXException {
    InputStream fileStream = getClass().getResourceAsStream("mutations.xml");

    MutationReport report = new MutationReport(fileStream);

    assertThat(report.getKillRatio(), is(new Ratio(32,329)));
  }

  @Test
  public void canDigestAMutation() throws IOException, SAXException {
    MutationReport report = new MutationReport(new ByteArrayInputStream(MUTATIONS.getBytes()));

    List<Mutation> mutations = report.getMutations();

    assertThat(mutations.size(), is(2));

    Mutation m1 = mutations.get(0);
    assertThat(m1.isDetected(), is(true));
    assertThat(m1.getLineNumber(), is(54));
    assertThat(m1.getStatus(), is("NO_COVERAGE"));
    assertThat(m1.getSourceFile(), is("SafeMultipartFile.java"));
    assertThat(m1.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
    assertThat(m1.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));

    Mutation m2 = mutations.get(1);
    assertThat(m2.isDetected(), is(false));
    assertThat(m2.getLineNumber(), is(57));
    assertThat(m2.getStatus(), is("KILLED"));
    assertThat(m2.getSourceFile(), is("SafeMultipartFile.java"));
    assertThat(m2.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
    assertThat(m2.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));
  }

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
