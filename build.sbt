name := "Team8Paper"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  filters,
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

play.Project.playScalaSettings

scalaVersion := "2.10.4"

