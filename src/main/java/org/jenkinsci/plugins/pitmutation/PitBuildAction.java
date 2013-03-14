package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
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
    killRatio_ = report.getKillRatio();
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

  public Ratio getKillRatio() {
    return killRatio_;
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) killRatio_.asPercentage(), Messages._BuildAction_Description(killRatio_));
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
  private Ratio killRatio_;
  private Ratio failThreshold_;
}
