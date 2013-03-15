package org.jenkinsci.plugins.pitmutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class PitPublisher extends Recorder {
  @Extension
  public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

  @DataBoundConstructor
  public PitPublisher(String mutationStatsFile, float minimumKillRatio, boolean killRatioMustImprove) {
    mutationStatsFile_ = mutationStatsFile;
    killRatioMustImprove_ = killRatioMustImprove;
    minimumKillRatio_ = minimumKillRatio;
    buildConditions_ = new ArrayList<Condition>();
    buildConditions_.add(percentageThreshold(minimumKillRatio));
    if (killRatioMustImprove) {
      buildConditions_.add(mustImprove());
    }
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException,
          InterruptedException {
    listener_ = listener;

    if (build.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
      listener_.getLogger().println("Looking for PIT report in " + build.getModuleRoot().getRemote());

      FilePath[] reports = locateMutationReports(build.getModuleRoot());

      if (reports.length == 0) {
        listener_.getLogger().println("No PIT mutation reports found. Searched '" + getMutationStatsFile() + "'.");
        build.setResult(Result.FAILURE);
        return true;
      }
      else {
        listener.getLogger().println("Found report: " + reports[0]);
        //publish latest report
        final FilePath targetPath = new FilePath(new FilePath(build.getRootDir()), "mutations.xml");
        reports[reports.length-1].copyTo(targetPath);
        PitBuildAction action = new PitBuildAction(build);
        build.getActions().add(action);
        build.setResult(decideBuildResult(action));
      }
    }
    return true;
  }

  /**
   * @return the worst result from all conditions
   */
  public Result decideBuildResult(PitBuildAction action) {
    Result result = Result.SUCCESS;
    for (Condition condition : buildConditions_) {
      Result conditionResult = condition.decideResult(action);
      result = conditionResult.isWorseThan(result) ? conditionResult : result;
    }
    return result;
  }

  public float getMinimumKillRatio() {
    return minimumKillRatio_;
  }

  public boolean getKillRatioMustImprove() {
    return killRatioMustImprove_;
  }

  public String getMutationStatsFile() {
    return mutationStatsFile_;
  }

  private Condition percentageThreshold(final float percentage) {
    return new Condition() {
      public Result decideResult(PitBuildAction action) {
        Ratio killRatio = action.getKillRatio();
        listener_.getLogger().println("Kill ratio is " + killRatio.asPercentage() +"% ("
                                      + killRatio.getNumerator() + " / " + killRatio.getDenominator() +")");
        return action.getKillRatio().asPercentage() >= percentage ? Result.SUCCESS : Result.FAILURE;
      }
    };
  }

  private Condition mustImprove() {
    return new Condition() {
      public Result decideResult(final PitBuildAction action) {
        PitBuildAction previousAction = action.getPreviousAction();
        if (previousAction != null) {
          listener_.getLogger().println("Previous kill ratio was " + previousAction.getKillRatio() + "%");
          return action.getKillRatio().compareTo(previousAction.getKillRatio()) < 0 ? Result.FAILURE : Result.SUCCESS;
        }
        else {
          return Result.SUCCESS;
        }
      }
    };
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
    return DESCRIPTOR;
  }

  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

//  static File[] getPitReports(AbstractBuild<?, ?> build) {
//    return build.getRootDir().listFiles(COBERTURA_FILENAME_FILTER);
//  }

  private FilePath[] locateMutationReports(FilePath root) throws IOException, InterruptedException {
     return root.list(mutationStatsFile_);
  }

  private List<Condition> buildConditions_;
  private String mutationStatsFile_;
  private String mutationResultsFile_;
  private boolean killRatioMustImprove_;
  private float minimumKillRatio_;
  private transient BuildListener listener_;

  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public DescriptorImpl() {
      super(PitPublisher.class);
    }

    @Override
    public String getDisplayName() {
      return Messages.PitPublisher_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindParameters(this, "pitmutation");
      save();
      return super.configure(req, formData);
    }

    /**
     * Creates a new instance of {@link PitPublisher} from a submitted form.
     */
    @Override
    public PitPublisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
      PitPublisher instance = req.bindJSON(PitPublisher.class, formData);
      return instance;
    }
  }
}
