package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author edward
 */
public class PitBuildAction implements HealthReportingAction, StaplerProxy {

  public PitBuildAction(AbstractBuild<?,?> owner) {
    owner_ = owner;
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

  public synchronized MutationReport getReport() {
    if (report_ == null) {
      report_ = readReport();
    }
    return report_;
  }

  private MutationReport readReport() {
    try {
      FilePath[] files = new FilePath(new FilePath(owner_.getRootDir()), "mutation-reports").list("mutations.xml");

      if (files.length < 1) {
        logger.log(Level.WARNING, "Could not find mutations.xml in " + owner_.getRootDir());
      }

      return MutationReport.create(files[0].read());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new MutationReport();
  }

  public Ratio getKillRatio() {
    return report_.getKillRatio();
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) getReport().getKillRatio().asPercentage(),
            Messages._BuildAction_Description(getReport().getKillRatio()));
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

  private static final Logger logger = Logger.getLogger(PitBuildAction.class.getName());

  private AbstractBuild<?, ?> owner_;
  private MutationReport report_;
}
