import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

repositories {
	mavenCentral()
}

apply {
	plugin("org.springframework.boot")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux:2.3.4.RELEASE")

//	implementation("commons-io:commons-io:jar:2.8.0")
	implementation("net.sf.jmimemagic:jmimemagic:0.1.5")
	implementation("ch.qos.logback:logback-classic:1.2.3")

//	annotationProcessor("org.projectlombok:lombok")
//	implementation("org.projectlombok:lombok:1.18.12")

	implementation("org.asciidoctor:asciidoctorj:2.4.1")
	implementation("org.asciidoctor:asciidoctorj-diagram:2.0.5")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
