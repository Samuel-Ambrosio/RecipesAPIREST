name := "RecipesAPIREST"

version := "0.1"

scalaVersion := "2.13.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
libraryDependencies += guice
libraryDependencies += evolutions
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.21"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"