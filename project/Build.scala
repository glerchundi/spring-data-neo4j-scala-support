import sbt._
import Keys._

object ApplicationBuildSettings {

  //
  // VERSIONS
  //

  val versions = new {

    val spring = new {
      val data = new {
        val neo4j         = "3.0.0.RELEASE"
      }
    }
    
    val junit             = "4.11"
    val scalatest         = "1.9.1"
    val scalamock         = "3.0.1"
  }

}

object ApplicationBuild extends Build {

  import ApplicationBuildSettings._

  // 
  // SPRING DATA NEO4J - SCALA SUPPORT -
  //

  val springDataNeo4jScalaSupport =
    Project(
      "spring-data-neo4j-scala-support",
      file("."),
      settings =
        Defaults.defaultSettings ++
        Seq(
          organization        :=  "org.springframework.data",
          version             :=  versions.spring.data.neo4j,
          scalaVersion        :=  "2.10.1",
          scalacOptions       ++= Seq( "-deprecation", "-unchecked", "-feature" ),
          conflictWarning     :=  ConflictWarning.default("global").copy(level = Level.Debug),
          resolvers           ++= {
            Seq(
              "Neo4j Cypher DSL Repository" at "http://m2.neo4j.org/content/repositories/releases"
            )
          },
          libraryDependencies <++= (scalaVersion)(sv =>
            Seq(
              "org.scala-lang"               %  "scala-reflect"               % sv,
              "org.springframework.data"     %  "spring-data-neo4j"           % versions.spring.data.neo4j,

              "junit"                        %  "junit"                       % versions.junit       % "test",
              "org.scalatest"                %% "scalatest"                   % versions.scalatest   % "test",
              "org.scalamock"                %% "scalamock-scalatest-support" % versions.scalamock   % "test"
            )
          )
        )
    )

}
