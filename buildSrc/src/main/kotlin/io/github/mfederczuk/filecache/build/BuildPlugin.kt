/*
 * Copyright 2021 Michael Federczuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mfederczuk.filecache.build

import com.android.build.gradle.TestedExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.io.FileInputStream
import java.util.Properties

class BuildPlugin : Plugin<Project> {

	private companion object {
		const val COMPILE_SDK_VERSION = 30
		const val TARGET_SDK_VERSION = 30
		const val DEFAULT_MIN_SDK_VERSION = 14

		const val EXPLICIT_API_MODE_ARG = "-Xexplicit-api=strict"
	}

	private object PluginId {
		const val AndroidLib = "com.android.library"
		const val AndroidApp = "com.android.application"
		const val KotlinAndroid = "kotlin-android"
		const val JetbrainsDokka = "org.jetbrains.dokka"
		const val MavenPublish = "maven-publish"
	}


	override fun apply(project: Project) {
		val isLibrary =
			when {
				project.pluginManager.hasPlugin(PluginId.AndroidLib) -> true
				project.pluginManager.hasPlugin(PluginId.AndroidApp) -> false
				else                                                 -> {
					error("Project $project must have either the '${PluginId.AndroidLib}' or '${PluginId.AndroidApp}' plugin applied")
				}
			}

		val buildPluginExtension = project.extensions.create("buildPlugin", BuildPluginExtension::class)

		project.configureAndroidPlugin(isLibrary, buildPluginExtension)

		project.pluginManager.apply(PluginId.KotlinAndroid)
		project.configureKotlinPlugin(isLibrary)

		if(isLibrary) {
			project.pluginManager.apply(PluginId.JetbrainsDokka)
			project.configureDokkaPlugin()

			val android = project.extensions.getByType<TestedExtension>()

			val generateSourcesJar = project.tasks.register("generateSourcesJar", Jar::class.java) {
				archiveClassifier.set("sources")
				from(android.sourceSets["main"].java.srcDirs)
			}

			val generateJavadocJar = project.tasks.register("generateJavadocJar", Jar::class.java) {
				val dokkaJavadoc = project.tasks.getByName<DokkaTask>("dokkaJavadoc")

				dependsOn(dokkaJavadoc)

				archiveClassifier.set("javadoc")
				from(dokkaJavadoc.outputDirectory)
			}

			project.pluginManager.apply(PluginId.MavenPublish)
			project.configureMavenPublishPlugin(generateSourcesJar, generateJavadocJar)
		}
	}


	private fun Project.configureAndroidPlugin(isLibrary: Boolean, buildPluginExtension: BuildPluginExtension) {
		val extension = extensions.getByType<TestedExtension>()

		with(extension) {
			compileSdkVersion(COMPILE_SDK_VERSION)
			defaultConfig {
				minSdk = DEFAULT_MIN_SDK_VERSION
				targetSdk = TARGET_SDK_VERSION

				if(buildPluginExtension.coreLibraryDesugaringEnabled) {
					multiDexEnabled = true
				}

				testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
				if(isLibrary) {
					consumerProguardFiles("consumer-rules.pro")
				}
			}

			buildTypes {
				getByName("release") {
					isMinifyEnabled = false
					proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
				}
			}

			compileOptions {
				if(buildPluginExtension.coreLibraryDesugaringEnabled) {
					isCoreLibraryDesugaringEnabled = true
				}

				sourceCompatibility = JavaVersion.VERSION_11
				targetCompatibility = JavaVersion.VERSION_11
			}
		}

		dependencies {
			if(buildPluginExtension.coreLibraryDesugaringEnabled) {
				add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:1.1.5")
			}
		}

		afterEvaluate {
			val targetSdkApiLevel = extension.defaultConfig.targetSdkVersion!!.apiLevel
			check(targetSdkApiLevel == TARGET_SDK_VERSION) {
				"$project targeting Android SDK $targetSdkApiLevel. Remove the defaultConfig.targetSdkVersion from the build script"
			}

			check(buildPluginExtension.coreLibraryDesugaringEnabled || !extension.compileOptions.isCoreLibraryDesugaringEnabled) {
				"$project has core library desugaring enabled, but the buildPlugin.enableCoreLibraryDesugaring() function was not used"
			}

			checkJavaTargeting("compileOptions.sourceCompatibility", extension.compileOptions.sourceCompatibility)
			checkJavaTargeting("compileOptions.targetCompatibility", extension.compileOptions.targetCompatibility)
		}
	}

	private fun Project.configureKotlinPlugin(isLibrary: Boolean) {
		tasks.withType<KotlinJvmCompile> {
			kotlinOptions {
				jvmTarget = JavaVersion.VERSION_11.toString()

				if(isLibrary && EXPLICIT_API_MODE_ARG !in freeCompilerArgs) {
					freeCompilerArgs = freeCompilerArgs + EXPLICIT_API_MODE_ARG
				}
			}
		}

		dependencies {
			val dependencyVersions = Properties()
			rootProject.projectDir
				.resolve("dependency_versions.properties")
				.inputStream()
				.use(dependencyVersions::load)
			add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${dependencyVersions.getProperty("kotlin")}")
		}
	}

	private fun Project.configureDokkaPlugin() {
		tasks.withType<DokkaTask>().configureEach {
			moduleName.set(rootProject.name)
			dokkaSourceSets.forEach { sourceSet ->
				sourceSet.reportUndocumented.set(true)
			}
		}
	}

	private fun Project.configureMavenPublishPlugin(
		generateSourcesJar: TaskProvider<Jar>,
		generateJavadocJar: TaskProvider<Jar>
	) {
		afterEvaluate {
			with(project.extensions.getByType<PublishingExtension>()) {
				publications {
					create<MavenPublication>("release") {
						from(components["release"])

						artifact(generateSourcesJar)
						artifact(generateJavadocJar)

						groupId = project.group.toString()
						artifactId = project.name
						version = project.version.toString()
						pom {
							name.set(rootProject.name)
							description.set("Android library to cache arbitrary data on the filesystem")
							url.set("https://github.com/mfederczuk/filecache")
							inceptionYear.set("2021")
							licenses {
								license {
									name.set("The Apache License, Version 2.0")
									url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
									distribution.set("repo")
								}
							}
							developers {
								developer {
									id.set("mfederczuk")
									name.set("Michael Federczuk")
									email.set("federczuk.michael@protonmail.com")
									url.set("https://github.com/mfederczuk")
									timezone.set("Europe/Vienna")
								}
							}
							scm {
								connection.set("scm:git:https://github.com/mfederczuk/filecache.git")
								developerConnection.set("scm:git:ssh://git@github.com/mfederczuk/filecache.git")
								tag.set("HEAD")
								url.set("https://github.com/mfederczuk/filecache")
							}
						}
					}
				}
			}
		}
	}


	private fun Project.checkJavaTargeting(variableName: String, actualJavaVersion: Any) {
		val version = JavaVersion.toVersion(actualJavaVersion)
		check(version.isJava11) {
			"$project targeting Java $version. Remove the $variableName from the build script"
		}
	}
}
