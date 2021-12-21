Procedure for building the SMT Plug-in with Maven
-------------------------------------------------

1. Download the Rodin target platform bundle from SourceForge (file named
   org.rodinp.dev-VERSION.zip) and unzip it into fresh folder "/tmp/rodin".

     mkdir /tmp/rodin
     cd /tmp
     wget http://sf.net/projects/rodin-b-sharp/files/Core_Rodin_Platform/VERSION/org.rodinp.dev-VERSION.zip/download
     cd rodin
     unzip ../org.rodinp.dev-VERSION.zip

2. Go to the directory containing this README file and type the command

     mvn -Dtycho.localArtifacts=ignore \
         -DrodinTargetSiteUrl=file://tmp/rodin \
	 -DforceContextQualifier="$(git log -1 --format='%h')" \
	 clean package

3. Upload to a new folder in Sourceforge all files of org.eventb.smt.site/target/repository,
   add a link to this folder in RodinUpdateSite/org.rodinp.updateSite/composite/compSite.xml
   (in the Subversion repository), regenerate the composite update site and
   upload the generated files to /home/project-web/rodin-b-sharp/htdocs/updates
   through Sourceforge's SFTP.
