val scala3Version = "3.0.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "casa",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.9" % Test,
      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    )
  )
