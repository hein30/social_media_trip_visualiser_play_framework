To setting up with different IDEs - https://www.playframework.com/documentation/2.5.x/IDE
In order to run the server, 
1)	a mongo database must be set up first using the data provided.
2)	Update the database credentials in conf/application.conf file’s following section.
mongodb {
  host = "localhost"
  port = 27017
  db = "play-test-app"
  user = "localtest"
  pw = "testing password"
}
3)	Update twitter and flickr keys and tokens for data collection.
4)	Install SBT, build environment of Play Framework. http://www.scala-sbt.org/download.html
5)	Navigate to the downloaded application folder directory. 
6)	type play run to load the application and start web server on port 9000.
7)	access it via localhost:9000/gridMap
