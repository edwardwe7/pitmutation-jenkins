package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;

import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author edward
 */
public class PitBuildAction implements HealthReportingAction, StaplerProxy {

  public PitBuildAction(AbstractBuild<?,?> owner) {
    owner_ = owner;
    report_ = getReport();
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

  public AbstractBuild<?,?> getOwner() {
    return owner_;
  }

  public MutationResult getTarget() {
    return getResult();
  }

  public MutationResult getResult() {
    return new MutationResult(this);
  }

  public MutationReport getReport() {
    try {
      return new MutationReport(new FileInputStream(owner_.getRootDir().listFiles(new FilenameFilter() {
        public boolean accept(File file, String name) {
          return "mutations.xml".equals(name);
        }
      })[0]));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Ratio getKillRatio() {
    return report_.getKillRatio();
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) report_.getKillRatio().asPercentage(),
            Messages._BuildAction_Description(report_.getKillRatio()));
  }

  public String getIconFileName() {
    return "/plugin/pitmutation/donatello.png";
  }

  public String getDisplayName() {
    return Messages.BuildAction_DisplayName();
  }

  public String getUrlName() {
    return "pitmutation";
  }

  private AbstractBuild<?, ?> owner_;
  private MutationReport report_;
}
