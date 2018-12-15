ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "Akka Http sample"


lazy val rootProject = (project in file("."))
  .settings(
    name := "Akka-Http-Sample",
    scalaSource in Compile := baseDirectory.value / "src",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    
    libraryDependencies += "com.typesafe" % "config" % "1.3.2",
    
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.5",
	libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12",
	libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",
	libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0",
	libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.5.0",

    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.20.0"
    
  )