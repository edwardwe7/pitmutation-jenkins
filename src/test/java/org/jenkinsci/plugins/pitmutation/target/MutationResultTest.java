package org.jenkinsci.plugins.pitmutation.target;

import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.ModuleResult;
import org.jenkinsci.plugins.pitmutation.targets.MutatedClass;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    Map<String, MutationReport> reportsNew = new HashMap<String, MutationReport>();
    Map<String, MutationReport> reportsOld = new HashMap<String, MutationReport>();
    reportsNew.put("test_report", reportNew);
    reportsOld.put("test_report", reportOld);


    PitBuildAction buildAction_ = mock(PitBuildAction.class);
    when(buildAction_.getReports()).thenReturn(reportsNew);
    PitBuildAction previousBuildAction_ = mock(PitBuildAction.class);
    when(previousBuildAction_.getReports()).thenReturn(reportsOld);
    when(buildAction_.getPreviousAction()).thenReturn(previousBuildAction_);

    ProjectMutations project = new ProjectMutations(buildAction_);
    result_ = new ModuleResult("test_module", project, reportNew);
  }

//  @Test
//  public void findsClassesWithNewSurvivors() {
//    Collection<MutatedClass> survivors = result_.getClassesWithNewSurvivors();
//    assertThat(survivors, hasSize(2));
//
//    Iterator<MutatedClass> it = survivors.iterator();
//    MutatedClass a = it.next();
//    MutatedClass b = it.next();
//    if (a.getFileName().equals("Mutation.java.html")) {
//      checkMutationJava(a);
//      checkPitBuildActionJava(b);
//    }
//    else {
//      checkMutationJava(b);
//      checkPitBuildActionJava(a);
//    }
//  }
//
//  private void checkMutationJava(MutatedClass mutant) {
//    assertThat(mutant.getFileName(), is("Mutation.java.html"));
//    assertThat(mutant.getMutatedLines(), hasSize(1));
//  }
//
//  private void checkPitBuildActionJava(MutatedClass mutant) {
//    assertThat(mutant.getFileName(), is("PitBuildAction.java.html"));
//    assertThat(mutant.getMutatedLines(), hasSize(2));
//  }

    @Test
    public void findsMutationsOnPitParserClass() {
      MutatedClass pitParser = result_.getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser");
      assertThat(pitParser.getChildren(), hasSize(3));
    }

    @Test
    public void collectsMutationStats() {
      MutationStats stats = result_.getMutationStats();
      assertThat(stats.getTotalMutations(), is(19));
      assertThat(stats.getUndetected(), is(15));
    }
//
//  @Test
//  public void collectsStatsOnNewTargets() {
//    Collection<MutationStats> newTargetStats = result_.getStatsForNewTargets();
//    assertThat(newTargetStats, hasSize(1));
//
//    MutationStats stats = newTargetStats.iterator().next();
//    assertThat(stats.getTotalMutations(), is(1));
//    assertThat(stats.getUndetected(), is(1));
//  }

  public ModuleResult getResult() {
    return result_;
  }

  private ModuleResult result_;
}
