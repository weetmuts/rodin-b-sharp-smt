How to publish the SMT Performance Test Application
---------------------------------------------------

Open file [org.eventb.smt.core.perf.app-site/site.xml] and click [Build All].

Then, export select all the elements of the
[org.eventb.smt.core.perf.app-site] project and click [Export...] in the
context menu. In the [Export] pop-up window, select [General/Archive File],
click [Next], enter the path to the archive file (must end in [.zip]), ensure
that [Create only selected directories] is checked and click [Finish].

The given archive file now contains an update site that can be used with p2 to
install the application in a Rodin platform.
