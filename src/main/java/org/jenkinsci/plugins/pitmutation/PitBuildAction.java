package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;

import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;

/**
 * @author edward
 */
public class PitBuildAction implements HealthReportingAction, StaplerProxy {

  public PitBuildAction(AbstractBuild<?,?> owner, MutationReport report) {
    owner_ = owner;
    report_ = report;
  }

  public PitBuildAction getPreviousAction() {
    AbstractBuild<?,?> b = owner_;
    while(true) {
      b = b.getPreviousBuild();
      if(b==null)
        return null;
      if(b.getResult() == Result.FAILURE)
        continue;
      PitBuildAction r = b.getAction(PitBuildAction.class);
      if(r != null)
        return r;
    }
  }

  public MutationResult getTarget() {
    return getResult();
  }

  public MutationResult getResult() {
    return report_.getMutationResult(owner_);
  }

  public Ratio getKillRatio() {
    return report_.getKillRatio();
  }

  public HealthReport getBuildHealth() {
    if (report_ == null) {
      return new HealthReport(1000000, "report was null");
    }
    if (report_.getKillRatio() == null) {
      return new HealthReport(1000000, "ratio was null");
    }
    return new HealthReport((int) report_.getKillRatio().asPercentage(),
            Messages._BuildAction_Description(report_.getKillRatio()));
  }

  public String getIconFileName() {
    return "/plugin/pit/donatello.png";
  }

  public String getDisplayName() {
    return Messages.BuildAction_DisplayName();
  }

  public String getUrlName() {
    return "pitmutation";
  }


  private AbstractBuild<?, ?> owner_;
  private MutationReport report_;
  private Ratio failThreshold_;
}
