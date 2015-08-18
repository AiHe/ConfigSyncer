name := "configsyncer"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.twitter" %% "util-zk-common" % "6.25.0"

libraryDependencies += "com.twitter" %% "util-zk" % "6.25.0"

libraryDependencies +=
  "org.apache.zookeeper" % "zookeeper" % "3.3.4" excludeAll(
    ExclusionRule(name = "jms"),
    ExclusionRule(name = "jmxtools"),
    ExclusionRule(name = "jmxri")
    )

resolvers += "twitter.com" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api"       % "1.7.7",
  "org.slf4j" % "jcl-over-slf4j"  % "1.7.7"
).map(_.force())

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14")) }

libraryDependencies += "net.contentobjects.jnotify" % "jnotify" % "0.94"
