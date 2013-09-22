package org.jenkinsci.plugins.pitmutation;

import hudson.model.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * @author Ed Kimber
 */
public class PitProjectAction extends Actionable implements ProminentProjectAction {
  private final AbstractProject<?, ?> project;

  public PitProjectAction(AbstractProject<?, ?> project) {
    this.project = project;

    PitPublisher cp = (PitPublisher) project.getPublishersList().get(PitPublisher.DESCRIPTOR);
//    if (cp != null) {
//      onlyStable = cp.getOnlyStable();
//    }
  }

  /**
   * Getter for property 'lastResult'.
   *
   * @return Value for property 'lastResult'.
   */
  public PitBuildAction getLastResult() {
    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE)
        continue;
      PitBuildAction r = b.getAction(PitBuildAction.class);
      if (r != null)
        return r;
    }
    return null;
  }

  /**
   * Getter for property 'lastResult'.
   *
   * @return Value for property 'lastResult'.
   */
  public Integer getLastResultBuild() {
    for (AbstractBuild<?, ?> b =  project.getLastSuccessfulBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE)
        continue;
      PitBuildAction r = b.getAction(PitBuildAction.class);
      if (r != null)
        return b.getNumber();
    }
    return null;
  }

  public String getIconFileName() {
    return "/plugin/pitmutation/donatello.png";
  }

  public String getUrlName() {
    return "pitmutation";
  }

  public String getDisplayName() {
    return "PIT Mutation Report";
  }

  public String getSearchUrl() {
    return getUrlName();
  }

  public boolean isFloatingBoxActive() {
    return true;
  }

  public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
    Integer buildNumber = getLastResultBuild();
    if (buildNumber == null) {
      rsp.sendRedirect2("nodata");
    } else {
      rsp.sendRedirect2("../" + buildNumber + "/pitmutation");
    }
  }

  public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
    if (getLastResult() != null)
      getLastResult().doGraph(req, rsp);
  }
}
