import com.github.gradle.node.npm.task.NpmTask

plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.2.21"
	id("org.ec4j.editorconfig") version "0.1.0"
	id("com.github.node-gradle.node") version "7.1.0"
	id("com.github.ben-manes.versions") version "0.53.0" // THis is the latest version that supports Kotlin 1.x series, DO NOT UPGRADE
}

group = "com.glinboy"
version = "0.0.1-SNAPSHOT"
description = "Craft smarter resumes and cover letters and track your job search with AI"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

defaultTasks("bootRun")

// Function to load environment variables from .env files
fun loadEnvFile(profile: String, warnIfMissing: Boolean = false): Map<String, String> {
	val envVars = mutableMapOf<String, String>()
	val envFile = file(".env.$profile")
	if (envFile.exists()) {
		envFile.readLines()
			.filter { it.isNotBlank() && !it.trim().startsWith("#") }
			.forEach { line ->
				val parts = line.split("=", limit = 2)
				if (parts.size == 2) {
					val key = parts[0].trim()
					val value = parts[1].trim()
					System.setProperty(key, value)
					envVars[key] = value
				}
			}
		println("✓ Loaded environment variables from .env.$profile")
	} else if (warnIfMissing) {
		println("⚠ Warning: .env.$profile not found")
	}
	return envVars
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-aspectj")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("tools.jackson.module:jackson-module-kotlin")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
//	implementation("io.github.perplexhub:rsql-jpa-spring-boot-starter:7.0.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-cache-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-mail-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Node.js configuration
node {
	version = "24.14.0"
	download = true
	workDir = file("${project.projectDir}/.gradle/nodejs")
	npmWorkDir = file("${project.projectDir}/.gradle/npm")
}

// Frontend build tasks
val installFrontend = tasks.register<NpmTask>("installFrontend") {
	group = "frontend"
	description = "Install frontend dependencies"
	args = listOf("install")
}

val buildFrontend = tasks.register<NpmTask>("buildFrontend") {
	group = "frontend"
	description = "Build the frontend"
	dependsOn(installFrontend)
	args = listOf("run", "build")
	inputs.dir("src/main/webapp")
	outputs.dir("build/resources/main/static")
}

val cleanFrontend = tasks.register<Delete>("cleanFrontend") {
	group = "frontend"
	description = "Clean frontend build artifacts"
	delete("build/resources/main/static")
	delete("node_modules")
}

// Load environment variables based on active profile
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	doFirst {
		val profile = project.findProperty("profile")?.toString() ?: System.getenv("SPRING_PROFILES_ACTIVE") ?: "dev"
		val envVars = loadEnvFile(profile, warnIfMissing = true)
		// Set environment variables for the bootRun process
		envVars.forEach { (key, value) -> environment(key, value) }
		systemProperty("spring.profiles.active", profile)
		println("🚀 Starting application with profile: $profile")
	}
}

// Load environment variables for tests
tasks.withType<Test> {
	doFirst {
		val profile = project.findProperty("profile")?.toString() ?: "test"
		val envVars = loadEnvFile(profile, warnIfMissing = false)
		envVars.forEach { (key, value) -> systemProperty(key, value) }
		systemProperty("spring.profiles.active", profile)
	}
}

// Load environment variables for bootJar (useful for building with specific profile)
tasks.bootJar {
	dependsOn(buildFrontend)
	doFirst {
		val profile = project.findProperty("profile")?.toString() ?: "prod"
		loadEnvFile(profile, warnIfMissing = false)
	}
}

// Ensure frontend is built before processing resources
tasks.processResources {
	filesMatching("config/**") {
		expand(project.properties)
	}
	dependsOn(buildFrontend)
}

// Clean frontend artifacts when cleaning
tasks.clean {
	dependsOn(cleanFrontend)
}

tasks.getByName<Jar>("jar") {
	enabled = false
}
