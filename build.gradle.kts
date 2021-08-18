import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//////////////////////
// VARIABLES TO CHANGE

object Variables {
    // Note: On Linux, if you installed Starsector into ~/something, you have to write /home/<user>/ instead of ~/
    val starsectorDirectory = "C:/Games/Starsector"
    val modVersion = "0.7.4"
    val jarFileName = "ExtraSystemCore.jar"

    val modId = "extra_system_reloaded"
    val modName = "Extra System Reloaded"
    val author = "Created by Originem. Reloaded by Iridescens"
    val description = "Upgrade ships at non-hostile markets!"
    val gameVersion = "0.95a-RC15"
    val jars = arrayOf("jars/$jarFileName")
    val modPlugin = "extrasystemreloaded.Es_ModPlugin"
    val isUtilityMod = false
    val masterVersionFile = "https://raw.githubusercontent.com/githubname/githubrepo/master/$modId.version"
    val modThreadId = "13089"

    val modFolderName = modName.replace(" ", "-")

// Scroll down and change the "dependencies" part of mod_info.json, if needed
// LazyLib is needed to use Kotlin, as it provides the Kotlin Runtime
}
//////////////////////

// Note: On Linux, use "${Variables.starsectorDirectory}" as core directory
val starsectorCoreDirectory = "${Variables.starsectorDirectory}/starsector-core"
val starsectorModDirectory = "${Variables.starsectorDirectory}/mods"
val modInModsFolder = File("$starsectorModDirectory/${Variables.modFolderName}")
val modFiles = modInModsFolder.listFiles()

plugins {
    kotlin("jvm") version "1.3.60"
    java
}

version = Variables.modVersion

repositories {
    maven(url = uri("$projectDir/libs"))
    jcenter()
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })

    //compileOnly(fileTree("$starsectorModDirectory/LazyLib/jars") { include("*.jar") })
    compileOnly(fileTree("$starsectorModDirectory/Console Commands/jars") { include("*.jar") })

    // Starsector jars and dependencies
    implementation(fileTree(starsectorCoreDirectory) {
        include(
            "starfarer.api.jar",
            "starfarer.api-sources.jar",
            "starfarer_obf.jar",
            "fs.common_obf.jar",
            "json.jar",
            "xstream-1.4.10.jar",
            "log4j-1.2.9.jar",
            "lwjgl.jar",
            "lwjgl_util.jar"
        )
    })
}

tasks {
    named<Jar>("jar")
    {
        destinationDirectory.set(file("$rootDir/jars"))
        archiveFileName.set(Variables.jarFileName)
    }

    register("create-metadata-files") {
        val version = Variables.modVersion.split(".").let { javaslang.Tuple3(it[0], it[1], it[2]) }
        System.setProperty("line.separator", "\n") // Use LF instead of CRLF like a normal person

        File(projectDir, "mod_info.json")
            .writeText(
                """
                    # THIS FILE IS GENERATED BY build.gradle.kts. (Note that Starsector's json parser permits `#` for comments)
                    {
                        "id": "${Variables.modId}",
                        "name": "${Variables.modName}",
                        "author": "${Variables.author}",
                        "utility": "${Variables.isUtilityMod}",
                        "version": { "major":"${version._1}", "minor": "${version._2}", "patch": "${version._3}" },
                        "description": "${Variables.description}",
                        "gameVersion": "${Variables.gameVersion}",
                        "jars":[${Variables.jars.joinToString() { "\"$it\"" }}],
                        "modPlugin":"${Variables.modPlugin}",
                        "dependencies": [
                        ]
                    }
                """.trimIndent()
            )

        with(File(projectDir, "data/config/version/version_files.csv")) {
            this.parentFile.mkdirs()
            this.writeText(
                """
                    version file
                    ${Variables.modId}.version

                """.trimIndent()
            )
        }

        File(projectDir, "${Variables.modId}.version")
            .writeText(
                """
                    # THIS FILE IS GENERATED BY build.gradle.kts.
                    {
                        "masterVersionFile":"${Variables.masterVersionFile}",
                        "modName":"${Variables.modName}",
                        "modThreadId":${Variables.modThreadId},
                        "modVersion":
                        {
                            "major":${version._1},
                            "minor":${version._2},
                            "patch":${version._3}
                        }
                    }
                """.trimIndent()
            )


        with(File(projectDir, ".github/workflows/mod-folder-name.txt")) {
            this.parentFile.mkdirs()
            this.writeText(Variables.modFolderName)
        }
    }

    // If enabled, will copy your mod to the /mods directory when run (and whenever gradle syncs).
    // Disabled by default, as it is not needed if your mod directory is symlinked into your /mods folder.
    register<Copy>("install-mod") {
        val enabled = false;

        if (!enabled) return@register

        println("Installing mod into Starsector mod folder...")

        val destinations = listOf(modInModsFolder)

        destinations.forEach { dest ->
            copy {
                from(projectDir)
                into(dest)
                exclude(".git", ".github", ".gradle", ".idea", ".run", "gradle")
            }
        }
    }

}

sourceSets.main {
    java.setSrcDirs(listOf("src"))
}
// Compile to Java 6 bytecode so that Starsector can use it
java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}