name := "CalculateData"

version := "0.0.3"

scalaVersion := "2.12.15"

//libraryDependencies += groupID % artifactID % aversion

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "3.3.0" % "provided",
    "org.apache.spark" %% "spark-sql" % "3.3.0" % "provided"
)

//groupId = org.apache.spark
//artifactId = spark-sql-kafka-0-10_2.12
//version = 3.3.0