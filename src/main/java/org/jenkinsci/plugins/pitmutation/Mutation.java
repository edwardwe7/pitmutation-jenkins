package org.jenkinsci.plugins.pitmutation;

/**
 * @author edward
 */
public class Mutation {

  //TODO better equals
  public boolean equals(Mutation m) {
    return m.getMutatedClass().equals(getMutatedClass())
           && m.getMutatedMethod().equals(getMutatedMethod())
           && m.getLineNumber() == getLineNumber()
           && m.getMutator().equals(getMutator())
           && m.getStatus().equals(getStatus());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Mutation && this.equals((Mutation) o);
  }

  @Override
  public int hashCode() {
    return getMutatedClass().hashCode() ^ getMutatedClass().hashCode();
  }

  public boolean isDetected() {
    return detected_;
  }

  public void setDetected(final boolean detected) {
    detected_ = detected;
  }

  public String getStatus() {
    return status_;
  }

  public void setStatus(final String status) {
    status_ = status;
  }

  public String getSourceFile() {
    return file_;
  }

  public void setSourceFile(final String file) {
    file_ = file;
  }

  public String getMutatedClass() {
    return clsName_;
  }

  public void setMutatedClass(final String clsName) {
    clsName_ = clsName;
  }

  public String getMutatedMethod() {
    return method_;
  }

  public void setMutatedMethod(final String method) {
    method_ = method;
  }

  public int getLineNumber() {
    return lineNumber_;
  }

  public void setLineNumber(final int lineNumber) {
    lineNumber_ = lineNumber;
  }

  public String getMutator() {
    return mutator_;
  }

  public void setMutator(final String mutator) {
    mutator_ = mutator;
  }

  public int getIndex() {
    return index_;
  }

  public void setIndex(final int index) {
    index_ = index;
  }

  public String getKillingTest() {
    return killedTest_;
  }

  public void setKillingTest(final String killedTest) {
    killedTest_ = killedTest;
  }

  public String toString() {
    return file_ + ":" + lineNumber_ + " : " + status_ + " type:[" + mutator_ +"]";
  }

  enum Status {
    KILLED,
    NO_COVERAGE
  }

  private boolean detected_;
  private String status_;
  private String file_;
  private String clsName_;
  private String method_;
  private int lineNumber_;
  private String mutator_;
  private int index_;
  private String killedTest_;
}
