plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	jacoco
	id("org.sonarqube") version "5.1.0.4882"
	checkstyle
}

val springBootVersion = "3.3.5"
val lombokVersion = "1.18.30"
val datafakerVersion = "2.0.2"
val jsonUnitVersion = "3.2.2"
val instancioVersion = "3.3.0"
val mapstructVersion = "1.5.5.Final"
val jacksonNullableVersion = "0.2.6"
val springdocVersion = "2.6.0"
val sentryVersion = "7.14.0"

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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("net.datafaker:datafaker:$datafakerVersion")
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	implementation("org.openapitools:jackson-databind-nullable:$jacksonNullableVersion")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:$sentryVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
	testImplementation("org.instancio:instancio-junit:$instancioVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootVersion")
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
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
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.coverage.exclusions",
			"src/main/java/hexlet/code/dto/**," +
					"src/main/java/hexlet/code/model/**," +
					"src/main/java/hexlet/code/exception/**," +
					"src/main/java/hexlet/code/config/**," +
					"src/main/java/hexlet/code/component/DataSeeder.java," +
					"src/main/java/hexlet/code/controller/WelcomeController.java," +
					"src/main/java/hexlet/code/AppApplication.java" +
					"src/main/java/hexlet/code/mapper/TaskMapper.java," +
					"src/main/java/hexlet/code/util/JWTUtils.java," +
					"src/main/java/hexlet/code/service/CustomUserDetailsService.java," +
					"src/main/java/hexlet/code/controller/AuthenticationController.java"
		)
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
	}
}