name := "Team8Paper"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  filters,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

play.Project.playScalaSettings
