package org.jenkinsci.plugins.pitmutation;

import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;

/**
 * @author edward
 */
public class PitBuildAction implements HealthReportingAction, StaplerProxy {

  public PitBuildAction(AbstractBuild<?,?> owner, Ratio killRatio, Ratio failThreshold) {
    owner_ = owner;
    killRatio_ = killRatio;
    failThreshold_ = failThreshold;
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) killRatio_.asPercentage(), Messages._BuildAction_Description(killRatio_));
  }

  public String getIconFileName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getDisplayName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getUrlName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object getTarget() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private AbstractBuild<?, ?> owner_;
  private Ratio killRatio_;
  private Ratio failThreshold_;
}
