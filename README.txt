Procedure for building the SMT Plug-in with Maven
-------------------------------------------------

1. Download the Rodin target platform bundle from SourceForge (file named
   org.rodinp.dev-VERSION.zip).

   If possible, download the bundle for Rodin 3.5 (org.rodinp.dev-3.5.0-9f39f1653.zip)
   and put it in /var/tmp. Otherwise, copy the name and path to the bundle for
   the next step.

2. Go to the directory containing this README file and type the command

     mvn -Dtycho.localArtifacts=ignore \
	 -DforceContextQualifier="$(git log -1 --format='%h')" \
	 clean package

   If you could not use the Rodin 3.5 bundle or put it in /var/tmp, add the
   following option to the command line:

         -DrodinTargetSiteUrl='jar:file:/path/to/org.rodinp.dev-VERSION.zip!/'

3. Upload to a new folder in Sourceforge all files of org.eventb.smt.site/target/repository,
   add a link to this folder in RodinUpdateSite/org.rodinp.updateSite/composite/compSite.xml
   (in the Subversion repository), regenerate the composite update site and
   upload the generated files to /home/project-web/rodin-b-sharp/htdocs/updates
   through Sourceforge's SFTP.
