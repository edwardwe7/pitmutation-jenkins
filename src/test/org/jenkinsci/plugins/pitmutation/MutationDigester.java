package org.jenkinsci.plugins.pitmutation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author edward
 */
public class MutationDigester {
  @Test
  public void canDigestAMutation() throws IOException, SAXException {
    Digester digester = new Digester();
    digester.addObjectCreate("mutations/mutation", Mutation.class);
    digester.addSetNext("mutations/mutation", "add", "org.jenkinsci.plugins.pitmutation.Mutation");
    digester.addSetProperties("mutations/mutation");
    digester.addSetNestedProperties("mutations/mutation");
    List<Mutation> mutations= new ArrayList<Mutation>();
    digester.push(mutations);
    digester.parse(new ByteArrayInputStream(MUTATIONS.getBytes()));

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
