name          := "cosas"
organization  := "com.miodx.common"
version       := "0.10.1"
description   := "esas cosas raras con muchos tipos"

crossScalaVersions := Seq("2.11.11", "2.12.3")
scalaVersion  := crossScalaVersions.value.last

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq (
  "org.scalatest"  %% "scalatest" % "3.0.4" % Test
)

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")

// scoverage conf
// coverageEnabled := true
coverageMinimum := 90.0
coverageFailOnMinimum := true
coverageHighlighting := true
// ambiguous implicit trick => untestable
coverageExcludedPackages := "ohnosequences.cosas.Distinct;ohnosequences.cosas.NotSubtypeOf"

wartremoverErrors in (Test, compile) --= Seq(
  Wart.Product,
  Wart.Serializable
)

// for debugging
// scalacOptions ++= Seq("-Xlog-implicits")
