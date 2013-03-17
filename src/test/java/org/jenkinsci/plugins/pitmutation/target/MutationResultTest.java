package org.jenkinsci.plugins.pitmutation.target;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutatedClass;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Ed Kimber
 * Date: 14/03/13
 * Time: 21:42
 */
public class MutationResultTest {
  @Before
  public void setUp() throws IOException, SAXException {
    InputStream[] mutationsXml_ = new InputStream[2];
    mutationsXml_[0] = MutationReport.class.getResourceAsStream("mutations-00.xml");
    mutationsXml_[1] = MutationReport.class.getResourceAsStream("mutations-01.xml");
    MutationReport reportOld = MutationReport.create(mutationsXml_[0]);
    MutationReport reportNew = MutationReport.create(mutationsXml_[1]);

    PitBuildAction buildAction_ = mock(PitBuildAction.class);
    when(buildAction_.getReport()).thenReturn(reportNew);
    PitBuildAction previousBuildAction_ = mock(PitBuildAction.class);
    when(previousBuildAction_.getReport()).thenReturn(reportOld);
    when(buildAction_.getPreviousAction()).thenReturn(previousBuildAction_);

    result_ = new MutationResult(buildAction_);
  }

  @Test
  public void findsClassesWithNewSurvivors() {
    Collection<MutatedClass> survivors = result_.getClassesWithNewSurvivors();
    assertThat(survivors, hasSize(2));

    Iterator<MutatedClass> it = survivors.iterator();
    MutatedClass a = it.next();
    MutatedClass b = it.next();
    if (a.getFileName().equals("Mutation.java.html")) {
      checkMutationJava(a);
      checkPitBuildActionJava(b);
    }
    else {
      checkMutationJava(b);
      checkPitBuildActionJava(a);
    }
  }

  private void checkMutationJava(MutatedClass mutant) {
    assertThat(mutant.getFileName(), is("Mutation.java.html"));
    assertThat(mutant.getMutatedLines(), hasSize(1));
  }

  private void checkPitBuildActionJava(MutatedClass mutant) {
    assertThat(mutant.getFileName(), is("PitBuildAction.java.html"));
    assertThat(mutant.getMutatedLines(), hasSize(2));
  }

  @Test
  public void findsMutationsOnPitParserClass() {
    Collection<Mutation> mutations = result_.getMutationsForClass("org.jenkinsci.plugins.pitmutation.PitParser");
    assertThat(mutations, hasSize(3));
  }

  @Test
  public void collectsMutationStats() {
    MutationStats stats = result_.getOverallStats();
    assertThat(stats.getTotalMutations(), is(19));
    assertThat(stats.getUndetected(), is(15));
  }

  @Test
  public void collectsStatsOnNewTargets() {
    Collection<MutationStats> newTargetStats = result_.getStatsForNewTargets();
    assertThat(newTargetStats, hasSize(1));

    MutationStats stats = newTargetStats.iterator().next();
    assertThat(stats.getTotalMutations(), is(1));
    assertThat(stats.getUndetected(), is(1));
  }

  private MutationResult result_;
}
