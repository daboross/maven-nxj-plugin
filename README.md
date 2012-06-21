# Maven NXJ Plugin #

## Setup ##

You have to install the lejos jars in the local maven repo:

	mvn install:install-file -DgroupId=org.lejos -DartifactId=classes -Dversion=0.9.1-beta2 -Dpackaging=jar -Dfile=classes.jar
	mvn install:install-file -DgroupId=org.lejos -DartifactId=jtools -Dversion=0.9.1-beta2 -Dpackaging=jar -Dfile=jtools.jar
	mvn install:install-file -DgroupId=org.lejos -DartifactId=pctools -Dversion=0.9.1-beta2 -Dpackaging=jar -Dfile=pctools.jar
	mvn install:install-file -DgroupId=org.lejos -DartifactId=pccomm -Dversion=0.9.1-beta2 -Dpackaging=jar -Dfile=pccomm.jar