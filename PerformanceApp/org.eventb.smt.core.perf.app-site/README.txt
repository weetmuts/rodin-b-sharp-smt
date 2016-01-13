How to publish the SMT Performance Test Application
---------------------------------------------------

Open file [org.eventb.smt.core.perf.app-site/site.xml] and click [Build All].

Then, in order to export the update site to an archive, select the following elements
of the [org.eventb.smt.core.perf.app-site] project:
* features
* plugins
* artifacts.jar
* content.jar
and click [Export...] in the context menu.
In the [Export] pop-up window, select [General/Archive File],
click [Next], enter the path to the archive file (must end in [.zip]), ensure
that [Create only selected directories] is checked and click [Finish].

The given archive file now contains an update site that can be used with p2 to
install the application in a Rodin platform.


How to make a product with the SMT Performance Test Application
---------------------------------------------------------------

Make a new directory intended to contain the test product and projects.
Take a SMT Performance update site archive, made as explained above,
and copy it to the same directory.
Take a SMT update site archive, made from the result of a Maven build of the SMT feature.
Take a fresh rodin archive and extract it in this directory.

Then run the following commands from the test directory:

./rodin/rodin -nosplash -application org.eclipse.equinox.p2.director -repository http://methode-b.com/update_site/atelierb_provers -installIU com.clearsy.atelierb.provers.feature.group
./rodin/rodin -nosplash -application org.eclipse.equinox.p2.director -repository jar:file:$PWD/SMT.zip!/ -installIU org.eventb.smt.feature.group
./rodin/rodin -nosplash -application org.eclipse.equinox.p2.director -repository jar:file:$PWD/PerfApp.zip!/ -installIU org.eventb.smt.core.perf.app.feature.group

renaming "SMT.zip" (the SMT feature update site archive)
and "PerfApp.zip" (the SMT Performance update site archive)
accordingly if needed.


How to run the SMT Performance Test Application
-----------------------------------------------

After following the above steps to prepare the product, make a new directory named "projects"
under the main directory (beside the "rodin" directory).
In the "projects" directory, put the Rodin projects to be used for performance measurement.
These Rodin projects must be previously extracted (no archive) and built so as to contain
Proof Obligation files (*.bpo).

Under the main directory, put the SMT configuration file (see org.eventb.smt.core.perf.app/preferences.model).
Then, you are ready to launch the performance measurement:

./rodin/rodin -nosplash -application org.eventb.smt.core.perf.app.main perf_preferences

or, in order to save and watch the text results:
./rodin/rodin -nosplash -application org.eventb.smt.core.perf.app.main perf_preferences > result.txt & tail -f result.txt

where "perf_preferences" is the name of the SMT configuration file, to be adapted if needed.

It will create a subdirectory named "results" containing, for every project and
every prover configuration, the proofs (*.bpr) along with the proof statuses (*.bps).
This enables to further analyze the results (which POs were proven/not proven by which prover).
