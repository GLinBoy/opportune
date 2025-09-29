plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.ec4j.editorconfig") version "0.1.0"
	id("com.github.node-gradle.node") version "7.1.0"
}

group = "com.glinboy"
version = "0.0.1-SNAPSHOT"
description = "Craft smarter resumes and cover letters and track your job search with AI"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.12")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
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
	version = "22.18.0"
	download = true
	workDir = file("${project.projectDir}/.gradle/nodejs")
	npmWorkDir = file("${project.projectDir}/.gradle/npm")
}

// Frontend build tasks
val installFrontend = tasks.register<com.github.gradle.node.npm.task.NpmTask>("installFrontend") {
	description = "Install frontend dependencies"
	args = listOf("install")
}

val buildFrontend = tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildFrontend") {
	description = "Build the frontend"
	dependsOn(installFrontend)
	args = listOf("run", "build")
	inputs.dir("src/main/webapp")
	outputs.dir("build/resources/main/static")
}

val cleanFrontend = tasks.register<Delete>("cleanFrontend") {
	description = "Clean frontend build artifacts"
	delete("build/resources/main/static")
	delete("node_modules")
}

// Ensure frontend is built before processing resources
tasks.processResources {
	filesMatching("config/**") {
		expand(project.properties)
	}
	dependsOn(buildFrontend)
}

// Ensure frontend is built before bootJar
tasks.bootJar {
	dependsOn(buildFrontend)
}

// Clean frontend artifacts when cleaning
tasks.clean {
	dependsOn(cleanFrontend)
}
