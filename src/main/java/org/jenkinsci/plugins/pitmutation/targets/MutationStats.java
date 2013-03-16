package org.jenkinsci.plugins.pitmutation.targets;

/**
 * User: Ed Kimber
 * Date: 15/03/13
 * Time: 21:22
 */
public interface MutationStats {
  String getTitle();

  int getUndetected();

  int getTotalMutations();
}
