package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import hudson.model.Result;

import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

  public ProjectMutations getTarget() {
    return getReport();
  }

  public ProjectMutations getReport() {
    return new ProjectMutations(this);
  }

  public synchronized Map<String, MutationReport> getReports() {
    if (reports_ == null) {
      reports_ = readReports();
    }
    return reports_;
  }

  private Map<String, MutationReport> readReports() {
    Map<String, MutationReport> reports = new HashMap<String, MutationReport>();

    try {
      FilePath[] files = new FilePath(owner_.getRootDir()).list("mutation-report*/mutations.xml");

      if (files.length < 1) {
        logger.log(Level.WARNING, "Could not find mutation-report*/mutations.xml in " + owner_.getRootDir());
      }

      for (int i = 0; i < files.length; i++) {
        logger.log(Level.WARNING, "Creating report for file: " + files[i].getRemote());
        reports.put(String.valueOf(i), MutationReport.create(files[i].read()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return reports;
  }

  /**
   * Getter for property 'previousResult'.
   *
   * @return Value for property 'previousResult'.
   */
  public PitBuildAction getPreviousResult() {
    return getPreviousResult(owner_);
  }

  /**
   * Gets the previous {@link PitBuildAction} of the given build.
   */
  static PitBuildAction getPreviousResult(AbstractBuild<?, ?> start) {
    AbstractBuild<?, ?> b = start;
    while (true) {
      b = b.getPreviousNotFailedBuild();
      if (b == null) {
        return null;
      }
      assert b.getResult() != Result.FAILURE : "We asked for the previous not failed build";
      PitBuildAction r = b.getAction(PitBuildAction.class);
      if (r != null) {
        return r;
      }
    }
  }

  public HealthReport getBuildHealth() {
    return new HealthReport((int) getReport().getMutationStats().getKillPercent(),
            Messages._BuildAction_Description(getReport().getMutationStats().getKillPercent()));
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

  /**
   * Generates the graph that shows the coverage trend up to this report.
   */
  public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
    if (ChartUtil.awtProblemCause != null) {
      // not available. send out error message
      rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
      return;
    }

    Calendar t = owner_.getTimestamp();

    if (req.checkIfModified(t, rsp)) {
      return; // up to date
    }
    DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();


    final JFreeChart chart = ChartFactory.createLineChart(null, // chart title
            null, // unused
            "%", // range axis label
            dsb.build(), // data
            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true, // tooltips
            false // urls
    );//    JFreeChart chart = new MutationChart(this).createChart();
    ChartUtil.generateGraph(req, rsp, chart, 500, 200);
  }

  private static final Logger logger = Logger.getLogger(PitBuildAction.class.getName());

  private AbstractBuild<?, ?> owner_;
  private Map<String, MutationReport> reports_;
}
