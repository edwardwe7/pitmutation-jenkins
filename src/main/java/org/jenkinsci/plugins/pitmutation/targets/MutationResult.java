package org.jenkinsci.plugins.pitmutation.targets;

import java.io.Serializable;

import hudson.model.AbstractBuild;

/**
 * @author edward
 */
public class MutationResult implements Serializable {
  public MutationResult(AbstractBuild<?,?> owner) {
    owner_ = owner;
  }

  public AbstractBuild getOwner() {
    return owner_;
  }

  private AbstractBuild<?,?> owner_;
}
