resolvers ++= Seq(
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  "repo.jenkins-ci.org" at "https://repo.jenkins-ci.org/public",
  Resolver.jcenterRepo
)

addSbtPlugin("clonomap" % "nice-sbt-settings" % "0.10.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
// addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.10")
