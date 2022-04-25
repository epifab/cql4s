val scala3Version = "3.1.1"

Global / version           := "SNAPSHOT"
Global / organization      := "solutions.epifab"
Global / homepage          := Some(url("https://github.com/epifab/cql4s"))
Global / scmInfo           := Some(ScmInfo(url("https://github.com/epifab/cql4s"), "git@github.com:epifab/cql4s.git"))
Global / developers        := List(Developer("epifab", "Fabio Epifani", "info@epifab.solutions", url("https://www.epifab.solutions")))
Global / licenses          += ("MIT", url("https://opensource.org/licenses/MIT"))
Global / fork              := true
Global / scalaVersion      := scala3Version

val scalaTestVersion     = "3.2.11"
val circeVersion         = "0.14.1"
val fs2Version           = "3.2.7"
val catsVersion          = "2.7.0"
val catsEffectVersion    = "3.3.11"
val zioVersion           = "2.0.0-RC5"

val commonDependencies = Seq(
  "com.datastax.oss" % "java-driver-core" % "4.14.0",
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

lazy val `cql4s-core` =
  project
    .in(file("cql4s-core"))
    .settings(
      libraryDependencies ++= commonDependencies
    )

lazy val `cql4s-circe` =
  project
    .in(file("cql4s-circe"))
    .dependsOn(`cql4s-core`)
    .settings(
      libraryDependencies ++= commonDependencies ++ Seq(
        "io.circe" %% "circe-core"    % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser"  % circeVersion,
      )
    )

lazy val `cql4s-cats` =
  project
    .in(file("cql4s-cats"))
    .dependsOn(`cql4s-core` % "compile->compile;test->test")
    .settings(
      libraryDependencies ++= Seq(
        "co.fs2"        %% "fs2-core"    % fs2Version,
        "co.fs2"        %% "fs2-io"      % fs2Version,
        "org.typelevel" %% "cats-core"   % catsVersion,
        "org.typelevel" %% "cats-effect" % catsEffectVersion
      ) ++ commonDependencies
    )

lazy val `cql4s-zio` =
  project
    .in(file("cql4s-zio"))
    .dependsOn(`cql4s-core` % "compile->compile;test->test")
    .settings(
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"         % zioVersion,
        "dev.zio" %% "zio-streams" % zioVersion
      ) ++ commonDependencies
    )
