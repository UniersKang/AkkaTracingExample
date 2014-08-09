organization := "akkatracingexample"

name := "AkkaTracingExample"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.github.levkhomich" %% "akka-tracing-core" % "0.3",
  "com.typesafe" % "config" % "1.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "com.typesafe.akka" %% "akka-cluster" % "2.3.2"
)