package org.jenkinsci.plugins.pitmutation;

import java.io.IOException;

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

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author edward
 */
public class PitReport extends Recorder {
  @Extension
  public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

  @DataBoundConstructor
  public PitReport(String mutationStatsFile, boolean failKillRatio, boolean autoUpdateKillRatio) {
    mutationStatsFile_ = mutationStatsFile;
    failKillRatio_ = failKillRatio;
    autoUpdateKillRatio_ = autoUpdateKillRatio;
    mutationTarget_ = new MutationTarget();
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException,
          InterruptedException {

    if (build.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
      listener.getLogger().println("Publishing PIT report");

      FilePath[] reports = locateMutationReports(build.getModuleRoot());

      if (reports.length == 0) {
        listener.getLogger().println("No PIT mutation reports found.");
        build.setResult(Result.FAILURE);
        return true;
      }
      else {
        try {
          PitParser parser = new PitParser(reports[0]);

          PitBuildAction action = new PitBuildAction(build, parser.getKillRatio());
          build.getActions().add(action);

          PitBuildAction previousAction = action.getPreviousResult();
          if (previousAction == null) { //first build TODO what if failed
            build.setResult(Result.SUCCESS);
            return true;
          }

          Ratio killRatio = parser.getKillRatio();
          Ratio previousKillRatio = previousAction.getKillRatio();
          listener.getLogger().println("Kill ratio: " + killRatio);
          listener.getLogger().println("Previous kill ratio:" + previousKillRatio);

          build.setResult(
                  killRatio.compareTo(previousKillRatio) < 0
                          ? Result.FAILURE : Result.SUCCESS);

        } catch (ParserConfigurationException e) {
          e.printStackTrace(listener.getLogger());
          build.setResult(Result.FAILURE);
        } catch (SAXException e) {
          e.printStackTrace(listener.getLogger());
          build.setResult(Result.FAILURE);
        }

      }
    }
    return true;
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
    return DESCRIPTOR;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  private FilePath[] locateMutationReports(FilePath root) throws IOException, InterruptedException {
     return root.list(mutationStatsFile_);
  }

  public String mutationStatsFile_;
  private boolean failKillRatio_;
  private boolean autoUpdateKillRatio_;
  private MutationTarget mutationTarget_;

  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public DescriptorImpl() {
      super(PitReport.class);
    }

    @Override
    public String getDisplayName() {
      return Messages.PitReport_DisplayName();
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
      req.bindParameters(this, "pitmutation.");
      save();
      return super.configure(req, formData);
    }

    /**
     * Creates a new instance of {@link PitReport} from a submitted form.
     */
    @Override
    public PitReport newInstance(StaplerRequest req, JSONObject formData) throws FormException {
      PitReport instance = req.bindJSON(PitReport.class, formData);
//      ConvertUtils.register(CoberturaPublisherTarget.CONVERTER, CoverageMetric.class);
//      List<CoberturaPublisherTarget> targets = req
//              .bindParametersToList(CoberturaPublisherTarget.class, "cobertura.target.");
//      instance.setTargets(targets);
      return instance;
    }

  }

}
