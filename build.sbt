name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs

// https://mvnrepository.com/artifact/org.mongodb.morphia/morphia
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.6"
// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-stream
libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.6"
// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-async
libraryDependencies += "org.twitter4j" % "twitter4j-async" % "4.0.6"
// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-media-support
libraryDependencies += "org.twitter4j" % "twitter4j-media-support" % "4.0.6"

// https://mvnrepository.com/artifact/com.flickr4java/flickr4java
libraryDependencies += "com.flickr4java" % "flickr4java" % "2.17"

// https://mvnrepository.com/artifact/net.sf.opencsv/opencsv
libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"

// https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.1"

// https://mvnrepository.com/artifact/org.geotools/gt-main
libraryDependencies += "org.geotools" % "gt-main" % "17.1"
// https://mvnrepository.com/artifact/org.geotools/gt-referencing
libraryDependencies += "org.geotools" % "gt-referencing" % "17.1"

// https://mvnrepository.com/artifact/nz.ac.waikato.cms.weka/weka-stable
libraryDependencies += "nz.ac.waikato.cms.weka" % "weka-stable" % "3.8.1"

// https://mvnrepository.com/artifact/org.orbisgis/jdelaunay
libraryDependencies += "org.orbisgis" % "jdelaunay" % "0.5.3"

resolvers += "Boundless" at "http://repo.boundlessgeo.com/main"
resolvers += "Osgeo Repo" at "http://download.osgeo.org/webdav/geotools/"

EclipseKeys.preTasks := Seq(compile in Compile)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)