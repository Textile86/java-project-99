plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	jacoco
	id("org.sonarqube") version "5.1.0.4882"
	checkstyle
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.3.5")
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")
	implementation("net.datafaker:datafaker:2.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	testImplementation("org.instancio:instancio-junit:3.3.0")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.14.0")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

checkstyle {
	toolVersion = "10.12.4"
}

sonar {
	properties {
		property("sonar.projectKey", "Textile86_java-project-99")
		property("sonar.organization", "textile86")
	}
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
	}
}