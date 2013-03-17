package org.jenkinsci.plugins.pitmutation;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.AbstractBuild;
import hudson.model.Result;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author edward
 */
public class PitBuildActionTest {
  private PitBuildAction action_;
  private AbstractBuild owner_;
  private AbstractBuild failedBuild_;
  private AbstractBuild successBuild_;
  private MutationReport report_;

  @Before
  public void setUp() {
    failedBuild_ = mock(AbstractBuild.class);
    when(failedBuild_.getResult()).thenReturn(Result.FAILURE);
    successBuild_ = mock(AbstractBuild.class);
    when(successBuild_.getResult()).thenReturn(Result.SUCCESS);
    owner_ = mock(AbstractBuild.class);
    File mockFileSys = mock(File.class);
    File mockFile = mock(File.class);
    File[] files = new File[1];
    files[0] = mockFile;
    when(mockFileSys.listFiles(any(FilenameFilter.class))).thenReturn(files);

    when(owner_.getRootDir()).thenReturn(mockFileSys);
    report_ = mock(MutationReport.class);
    action_ = new PitBuildAction(owner_);
  }

  @Test
  public void previousReturnsNullIfNoPreviousBuilds() {
    assertThat(action_.getPreviousAction(), nullValue());
  }

  @Test
  public void previousReturnsNullIfAllPreviousBuildsFailed() {
    when(owner_.getPreviousBuild()).thenReturn(failedBuild_);
    assertThat(action_.getPreviousAction(), nullValue());
  }

  @Test
  public void previousReturnsLastSuccessfulBuild() {
    PitBuildAction previousSucccessAction = mock(PitBuildAction.class);
    when(owner_.getPreviousBuild()).thenReturn(failedBuild_);
    when(failedBuild_.getPreviousBuild()).thenReturn(successBuild_);
    when(successBuild_.getAction(PitBuildAction.class)).thenReturn(previousSucccessAction);

    assertThat(action_.getPreviousAction(), is(previousSucccessAction));
  }
}
