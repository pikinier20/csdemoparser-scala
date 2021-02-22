name := "CSDemoParser"

version := "0.1"

scalaVersion := "2.13.4"

idePackagePrefix := Some("demoparser")

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion
)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

Compile / PB.protoSources := Seq(
  new File("src/main/protobuf/netmessages.proto"),
  new File("src/main/protobuf/cstrike15_usermessages.proto"),
  new File("src/main/protobuf/cstrike15_gcmessages.proto"),
  new File("src/main/protobuf/engine_gcmessages.proto"),
  new File("src/main/protobuf/steammessages.proto")
)

Compile / PB.includePaths := Seq(
  new File("src/main/protobuf")
)
