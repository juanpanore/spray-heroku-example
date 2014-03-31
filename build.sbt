import com.typesafe.sbt.SbtNativePackager.Universal

name:= "spray-heroku-example"

version       := "0.1"

scalaVersion := "2.10.3"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Kamon Repository" at "http://repo.kamon.io"
)

libraryDependencies ++= {
  val akkaV = "2.2.4"
  val sprayV = "1.2.1"
  val kamonV = "0.0.14"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-client"  % sprayV,
    "io.spray"           %%   "spray-json" 	  % "1.2.5",   
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "org.specs2"         %%   "specs2-core"   % "2.3.7" % "test",
    "com.newrelic.agent.java" % "newrelic-agent" % "3.4.2",
    "kamon"               %   "kamon-core"    % kamonV,
    "kamon"               %  "kamon-spray"    % kamonV,
    "kamon"               %  "kamon-newrelic" % kamonV,
    "org.aspectj"         % "aspectjweaver"   % "1.7.2"
  )
}

packageArchetype.java_application

mappings in Universal += {
  file("src/main/resources/application.conf") -> "conf/application.conf"
}

