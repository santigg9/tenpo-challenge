plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.tenpo"
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
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-aop:3.4.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.4.2")
	implementation("org.springframework.boot:spring-boot-starter-cache:3.4.2")

	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.4")

	implementation("jakarta.validation:jakarta.validation-api:3.1.0")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

	compileOnly("org.projectlombok:lombok:1.18.36")
	annotationProcessor("org.projectlombok:lombok:1.18.36")

	implementation("io.github.resilience4j:resilience4j-spring-boot2:2.3.0")
	implementation("io.github.resilience4j:resilience4j-reactor:2.3.0")
	implementation("io.github.resilience4j:resilience4j-retry:2.3.0")
	implementation("io.github.resilience4j:resilience4j-ratelimiter:2.3.0")





}

tasks.jar {
	manifest.attributes["Main-Class"] = "com.tenpo.TenpoApplication"
	val dependencies = configurations
		.runtimeClasspath
		.get()
		.map(::zipTree)
	from(dependencies)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Test> {
	useJUnitPlatform()
}
