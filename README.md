pitmutation-jenkins
===================

PIT Mutation reporting plugin for Jenkins.

Set up as a post-build step, after the PIT mutation tests have been run.

Configure report path, e.g. `target/pit-reports` for a maven build.

The plugin needs the XML and HTML output from PIT. Also make sure 
that a clean target is executed before building, otherwise PIT will 
keep all of the old reports and it may not pick up the right one.

The report will display some statistics and details of all new surviving 
mutations and coverage gaps that occurred since the last build, using the stan
