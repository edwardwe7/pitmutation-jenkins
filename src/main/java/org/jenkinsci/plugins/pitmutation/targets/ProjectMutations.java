package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.collect.Maps;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Ed Kimber
 */
public class ProjectMutations extends MutationResult<ProjectMutations> {
  public ProjectMutations(PitBuildAction action) {
    super("aggregate", null);
    action_ = action;
  }

  @Override
  public AbstractBuild<?,?> getOwner() {
    return action_.getOwner();
  }

  public ProjectMutations getPreviousResult() {
    return action_.getPreviousAction().getReport();
  }

  @Override
  public MutationStats getMutationStats() {
    return aggregateStats(action_.getReports().values());
  }

  private static MutationStats aggregateStats(Collection<MutationReport> reports) {
    MutationStats stats = new MutationStatsImpl("", new ArrayList<Mutation>(0));
    for (MutationReport report : reports) {
      stats = stats.aggregate(report.getMutationStats());
    }
    return stats;
  }

  @Override
  public String getName() {
    return "Aggregated Reports";
  }

  public String getDisplayName() {
    return "Modules";
  }

  public Map<String, ? extends MutationResult<?>> getChildMap() {
    return Maps.transformEntries(action_.getReports(), moduleTransformer_);
  }

  private Maps.EntryTransformer<String, MutationReport, ModuleResult> moduleTransformer_ =
          new Maps.EntryTransformer<String, MutationReport, ModuleResult>() {
    public ModuleResult transformEntry(String moduleName, MutationReport report) {
      return new ModuleResult(moduleName, ProjectMutations.this, report);
    }
  };

  public int compareTo(ProjectMutations other) {
    return this.getMutationStats().getUndetected() - other.getMutationStats().getUndetected();
  }

  private PitBuildAction action_;
}
