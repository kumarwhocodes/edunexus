plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.kumar"
version = "0.0.1-SNAPSHOT"
description = "Project - I"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Firebase Admin SDK Dependency
    implementation ("com.google.firebase:firebase-admin:8.1.0")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // MapStruct
    implementation ("org.mapstruct:mapstruct:1.6.3")

    // Annotation processors - order matters
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    //Validation
    implementation ("org.springframework.boot:spring-boot-starter-validation")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
