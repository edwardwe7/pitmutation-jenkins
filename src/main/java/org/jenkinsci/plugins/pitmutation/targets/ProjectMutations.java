package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.jenkinsci.plugins.pitmutation.utils.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: Ed Kimber
 */
public class ProjectMutations extends MutationResult {
  public ProjectMutations(PitBuildAction action) {
    super(action.getOwner(),
            new Pair<MutationStats>(
            aggregateStats(action.getReports().values()),
            aggregateStats(action.getPreviousAction().getReports().values())));
    action_ = action;
  }

  private static MutationStats aggregateStats(Collection<MutationReport> reports) {
    MutationStats stats = new MutationStatsImpl("", new ArrayList<Mutation>(0));
    for (MutationReport report : reports) {
      stats = stats.aggregate(report.getMutationStats());
    }
    return stats;
  }

  public Collection<ModuleResult> getModules() {
    Collection<ModuleResult> modules = new ArrayList<ModuleResult>();
    Map<String, MutationReport> reports = action_.getReports();
    Map<String, MutationReport> previous = action_.getPreviousAction().getReports();
    for (String moduleName : reports.keySet()) {
      modules.add(new ModuleResult(moduleName, getOwner(), new Pair<MutationReport>(
              reports.get(moduleName), previous.get(moduleName))));
    }
    return modules;
  }

  private PitBuildAction action_;
}
