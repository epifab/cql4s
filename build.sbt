val scala3Version = "3.0.2"


val scalaTestVersion     = "3.2.9"

val circeVersion         = "0.14.1"

val fs2Version           = "3.1.3"
val catsVersion          = "2.6.1"
val catsEffectVersion    = "3.2.9"

val zioVersion           = "2.0.0-M4"

val commonDependencies = Seq(
  "com.datastax.oss" % "java-driver-core" % "4.13.0",
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "cql4s-core",
      version := "0.1.0",

      fork := true,

      scalaVersion := scala3Version,

      libraryDependencies ++= commonDependencies
    )

lazy val circe =
  project
    .in(file("circe"))
    .dependsOn(core)
    .settings(
      name := "cql4s-core",
      version := "0.1.0",

      fork := true,

      scalaVersion := scala3Version,

      libraryDependencies ++= commonDependencies ++ Seq(
        "io.circe"         %% "circe-core"    % circeVersion,
        "io.circe"         %% "circe-generic" % circeVersion,
        "io.circe"         %% "circe-parser"  % circeVersion,
      )
    )

lazy val cats =
  project
    .in(file("cats"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "cql4s-cats",
      version := "0.1.0",

      fork := true,

      scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
        "co.fs2"           %% "fs2-core"      % fs2Version,
        "co.fs2"           %% "fs2-io"        % fs2Version,
        "org.typelevel"    %% "cats-core"     % catsVersion,
        "org.typelevel"    %% "cats-effect"   % catsEffectVersion
      ) ++ commonDependencies
    )

lazy val zio =
  project
    .in(file("zio"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "cql4s-zio",
      version := "0.1.0",

      fork := true,

      scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"         % zioVersion,
        "dev.zio" %% "zio-streams" % zioVersion
      ) ++ commonDependencies
    )
