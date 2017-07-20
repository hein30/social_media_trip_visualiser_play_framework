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

EclipseKeys.preTasks := Seq(compile in Compile)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java         
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)