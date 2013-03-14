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

  public MutationResult getResult() {
    return null; //TODO
  }

  public Ratio getKillRatio() {
    return report_.getKillRatio();
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) report_.getKillRatio().asPercentage(),
            Messages._BuildAction_Description(report_.getKillRatio()));
  }

  public String getIconFileName() {
    return null;
  }

  public String getDisplayName() {
    return Messages.BuildAction_DisplayName();
  }

  public String getUrlName() {
    return null;
  }

  public Object getTarget() {
    return null;
  }

  private AbstractBuild<?, ?> owner_;
  private MutationReport report_;
  private Ratio failThreshold_;
}
