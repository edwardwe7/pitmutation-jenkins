package org.jenkinsci.plugins.pitmutation;

import hudson.Launcher;
import hudson.model.BuildListener;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * User: Ed Kimber
 * Date: 17/03/13
 * Time: 17:55
 */
public class PitPublisherTest {

  @Before
  public void setup() {
    publisher_ = new PitPublisher("**/mutations.xml", minimumKillRatio_, true);

//    , mock(Launcher.class), mock(BuildListener.class)
  }

//  @Test
//  public void mutationReportPresenceCheck() {
//    publisher_.mutationsReportExists();
//  }

  private PitPublisher publisher_;
  private float minimumKillRatio_ = 0.25f;

}
