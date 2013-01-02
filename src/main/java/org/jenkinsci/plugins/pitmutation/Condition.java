package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;

/**
 * @author edward
 */
interface Condition {
  Result decideResult(PitBuildAction action);
}
