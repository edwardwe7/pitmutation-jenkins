package org.jenkinsci.plugins.pitmutation.dashboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * @author edward
 */
public class MutationPortlet extends DashboardPortlet {

  @DataBoundConstructor
  public MutationPortlet(String name) {
    super(name);
  }

  public Collection<Run> getCoverageRuns() {
    LinkedList<Run> allResults = new LinkedList<Run>();

    for (Job job : getDashboard().getJobs()) {
      // Find the latest successful coverage data
      Run run = job.getLastSuccessfulBuild();
      if (run == null) {
        continue;
      }

      PitBuildAction pitBuildAction = run.getAction(PitBuildAction.class);

      if (pitBuildAction != null) {
        allResults.add(run);
      }
    }

    return allResults;
  }

  public MutationResult getCoverageResult(Run run) {
    PitBuildAction pitBuildAction = run.getAction(PitBuildAction.class);
    return pitBuildAction.getResult();
  }

//  public HashMap<MutationMetric, Ratio> getTotalCoverageRatio() {
//    HashMap<CoverageMetric, Ratio> totalRatioMap = new HashMap<CoverageMetric, Ratio>();
//    for (Job job : getDashboard().getJobs()) {
//      // Find the latest successful coverage data
//      Run run = job.getLastSuccessfulBuild();
//      if (run == null) {
//        continue;
//      }
//
//      CoberturaBuildAction rbb = run
//              .getAction(CoberturaBuildAction.class);
//
//      if (rbb == null) {
//        continue;
//      }
//
//      CoverageResult result = rbb.getResult();
//      Set<CoverageMetric> metrics = result.getMetrics();
//
//      for (CoverageMetric metric : metrics) {
//        if (totalRatioMap.get(metric) == null) {
//          totalRatioMap.put(metric, result.getCoverage(metric));
//        } else {
//          float currentNumerator = totalRatioMap.get(metric).numerator;
//          float CurrentDenominator = totalRatioMap.get(metric).denominator;
//          float sumNumerator = currentNumerator + result.getCoverage(metric).numerator;
//          float sumDenominator = CurrentDenominator + result.getCoverage(metric).denominator;
//          totalRatioMap.put(metric, Ratio.create(sumNumerator, sumDenominator));
//        }
//      }
//    }
//    return totalRatioMap;
//  }

  public static class DescriptorImpl extends Descriptor<DashboardPortlet> {

    @Extension(optional = true)
    public static DescriptorImpl newInstance() {
      if (Hudson.getInstance().getPlugin("dashboard-view") != null) {
        return new DescriptorImpl();
      } else {
        return null;
      }
    }

    @Override
    public String getDisplayName() {
      return "PIT Mutation View";
    }
  }
}
