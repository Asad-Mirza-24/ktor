/*
 * Copyright 2014-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.time.*

description = ""

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.datetime)
            }
        }
    }
}

val configuredVersion: String by rootProject.extra

val generateKtorVersionFile by tasks.registering {
    val ktorVersion = configuredVersion
    inputs.property("ktor_version", ktorVersion)

    val year = Year.now().toString()
    inputs.property("year", year)

    val generatedSourcesDirectory = layout.buildDirectory.dir("generated/src")
    outputs.dir(generatedSourcesDirectory)

    doFirst {
        val outputDirectory = generatedSourcesDirectory.get().asFile
        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()
        outputDirectory.resolve("KtorVersion.kt").writeText(
            """
            |/*
            | * Copyright 2014-$year JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
            | */
            |package io.ktor.server.plugins.defaultheaders
            |
            |// Generated by `generateKtorVersionFile` Gradle task
            |internal const val KTOR_VERSION: String = "$ktorVersion"
            |
            """.trimMargin()
        )
    }
}

// special task name which is called during idea import
tasks.maybeRegister("prepareKotlinIdeaImport") { dependsOn(generateKtorVersionFile) }

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateKtorVersionFile)
        }
    }
}
