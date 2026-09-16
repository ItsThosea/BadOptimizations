@file:Suppress("PropertyName", "LocalVariableName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import me.modmuss50.mpp.ModPublishExtension

plugins {
	id("java")
	id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT" apply false
	id("architectury-plugin") version "3.5-SNAPSHOT"
	id("com.gradleup.shadow") version "9.0.0" apply false
	id("me.modmuss50.mod-publish-plugin") version "0.5.0" apply false
}

val minecraft_version by properties
val java_version by properties
val mod_version by properties

architectury {
	minecraft = "$minecraft_version"
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "dev.architectury.loom-no-remap")
	apply(plugin = "architectury-plugin")
	apply(plugin = "me.modmuss50.mod-publish-plugin")

	base {
		archivesName = "BadOptimizations-${project.name}"
	}

	repositories {
		maven(url = "https://maven.terraformersmc.com/")
	}

	dependencies {
		val minecraft by configurations
		minecraft("com.mojang:minecraft:${minecraft_version}")
		implementation(annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.2")!!)
	}

	java.toolchain {
		languageVersion = JavaLanguageVersion.of("$java_version")
	}

	version = "$mod_version"
	group = "me.thosea"
}

val changelogText = file("changelog.md").readText()

subprojects {
	if(name == "common") return@subprojects
	apply(plugin = "com.gradleup.shadow")

	tasks.processResources {
		val properties = mapOf("version" to project.version)
		inputs.properties(properties)

		filesMatching(listOf(
			"fabric.mod.json",
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml"
		)) {
			expand(properties)
		}
	}

	val common by configurations.creating
	val shadowBundle by configurations.creating

	dependencies {
		common(project(":common"))
	}

	val jarName = "BadOptimizations-${mod_version}-${minecraft_version}-${name}.jar"

	tasks.jar {
		archiveClassifier = "no-shadow"
	}
	tasks.shadowJar {
		configurations = setOf(shadowBundle)
		archiveFileName = jarName
	}
	tasks.assemble {
		finalizedBy(tasks.shadowJar)
	}

	configurations {
		with(common) {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
		compileClasspath.get().extendsFrom(common)
		runtimeClasspath.get().extendsFrom(common)

		// Files in this configuration will be bundled into your mod using the Shadow plugin.
		// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
		with(shadowBundle) {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
	}

	extensions.configure<ModPublishExtension> {
		val platform = "${project.property("loom.platform")}"

		file = file("build/libs/${jarName}")
		displayName = "$mod_version (26.3/$platform)"

		version = "$mod_version"
		type = STABLE
		modLoaders.add(platform)

		// tokens from HOME/.gradle/gradle.properties

		val mr_token by properties
		val cf_token by properties

		modrinth {
			accessToken = "$mr_token"
			projectId = "g96Z4WVZ"
			minecraftVersions.add("26.3")
		}

		curseforge {
			accessToken = "$cf_token"
			projectId = "949555"
			minecraftVersions.add("26.3")
			clientRequired = true
		}

		changelog = changelogText

		tasks.getByName("publishCurseforge") { dependsOn(tasks.shadowJar) }
		tasks.getByName("publishModrinth") { dependsOn(tasks.shadowJar) }
	}
}

rootProject.tasks.jar {
	enabled = false
}