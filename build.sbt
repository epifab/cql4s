val scala3Version = "3.0.2"


val fs2Version           = "3.1.3"
val catsVersion          = "2.6.1"
val catsEffectVersion    = "3.2.9"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cql4s",
    version := "0.1.0",

    fork := true,

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.datastax.oss" % "java-driver-core" % "4.13.0",

      "co.fs2"           %% "fs2-core"    % fs2Version,
      "co.fs2"           %% "fs2-io"      % fs2Version,
      "org.typelevel"    %% "cats-core"   % catsVersion,
      "org.typelevel"    %% "cats-effect" % catsEffectVersion,

      "org.scalactic"    %% "scalactic"   % "3.2.9" % Test,
      "org.scalatest"    %% "scalatest"   % "3.2.9" % Test
    )
  )
