Procedure for building the SMT Plug-in with Maven
-------------------------------------------------

1. Download the Rodin target platform bundle from SourceForge (file named
   org.rodinp.dev-VERSION.zip) and unzip it into fresh folder "/tmp/rodin".

     mkdir /tmp/rodin
     cd /tmp
     wget http://sf.net/projects/rodin-b-sharp/files/Core_Rodin_Platform/VERSION/org.rodinp.dev-VERSION.zip/download
     cd rodin
     unzip ../org.rodinp.dev-VERSION.zip


2. Checkout the update site from Subversion:

     cd /tmp
     svn checkout svn+ssh://svn.code.sf.net/p/rodin-b-sharp/svn/trunk/RodinUpdateSite/org.rodinp.updateSite


3. Go to the directory containing this README file and type the command

     mvn -Dtycho.localArtifacts=ignore \
         -DrodinTargetSiteUrl=file://tmp/rodin \
	 -DrodinSiteMirror=/tmp/org.rodinp.updateSite \
	 -DforceContextQualifier="$(git log -1 --format='%h')" \
	 clean install

4. Upload to Source Forge all new jar files of features and plug-ins that have
   been added to /tmp/org.rodinp.updateSite and commit the new p2 files in Subversion.
