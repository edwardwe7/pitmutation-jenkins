package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;

import java.util.Collection;
import java.util.Set;

/**
 * User: Ed Kimber
 * Date: 15/03/13
 * Time: 02:31
 */
public class MutatedClass {
  private String name_;
  Collection<Mutation> mutations_;

  public MutatedClass(String name, Collection<Mutation> mutations) {
    name_ = name;
    mutations_ = mutations;
  }

  public String getName() {
    return name_;
  }

  public Collection<Mutation> getMutations() {
    return mutations_;
  }

}
