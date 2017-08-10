name := "Bounty"

version := "0.0.0"

scalaVersion := "2.11.8"


resolvers += "Spigot Snapshots" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots"
resolvers += "Bungeecord Chat" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies+= "org.spigotmc" % "spigot-api" % "1.12-R0.1-SNAPSHOT"

lazy val root = (project in file("."))