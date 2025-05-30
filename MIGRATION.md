# Spring Boot 2 to Spring Boot 3 Migration Guide

## Migration Progress Tracker

- [x] Ensure Java 17+ is installed
- [x] Upgrade Gradle wrapper (version 8.14.1 specified in build.gradle)
- [x] Update Spring Boot dependencies (to 3.0.13)
- [x] Add properties migrator
- [x] Update javax to jakarta packages (Product.java updated)
- [ ] Review and update configuration properties
- [x] Update Spring MVC/WebFlux configuration (if applicable)
- [ ] Update Actuator configuration (if applicable)
- [ ] Update Data Access code (if applicable)
- [x] Update Security configuration (replaced WebSecurityConfigurerAdapter with SecurityFilterChain bean)
- [ ] Update Testing code
- [ ] Build and Test
- [ ] Remove Properties Migrator

# Original Migration Guide

This document outlines the steps required to migrate a Spring Boot 2.x application to Spring Boot 3.0.

## Prerequisites

### 1. Ensure Java 17+ is installed

Spring Boot 3.0 requires Java 17 or later. Before beginning the migration, ensure your development environment has Java 17+ installed.

```bash
# Check your current Java version
java -version
```

If you're using a tool manager such as mise, asdf, or sdkman, you can install and set Java 17:

**Using mise:**
```bash
mise install java 17
mise use java 17
```

**Using asdf:**
```bash
asdf install java adoptopenjdk-17.0.X
asdf global java adoptopenjdk-17.0.X
```

**Using sdkman:**
```bash
sdk install java 17.0.X-tem
sdk use java 17.0.X-tem
```

### 2. Update to latest Spring Boot 2.7.x version

Before migrating to Spring Boot 3.0, update your application to the latest Spring Boot 2.7.x version. This ensures you're building against the most recent compatible dependencies and reduces the migration gap.

## Migration Steps

### 1. Upgrade Gradle Wrapper (if applicable)

Update your Gradle wrapper to the latest version:

```bash
./gradlew wrapper --gradle-version 8.14.1
```

The latest Gradle version as of now is 8.14.1, which supports Java up to version 23.

### 2. Update Spring Boot Dependencies

Update your build configuration to use Spring Boot 3.0.x. The latest version in the 3.0.x line is 3.0.13.

**For Gradle (build.gradle):**
```groovy
plugins {
    id 'org.springframework.boot' version '3.0.13'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}
```

**For Gradle (build.gradle.kts):**
```kotlin
plugins {
    id("org.springframework.boot") version "3.0.13"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.7.22" // If using Kotlin
    kotlin("plugin.spring") version "1.7.22" // If using Kotlin with Spring
}
```

**For Maven (pom.xml):**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.0.13</version>
    <relativePath/>
</parent>
```

### 3. Add the Properties Migrator (temporary)

Add the Spring Boot Properties Migrator to help identify and temporarily migrate deprecated properties:

**For Maven (pom.xml):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```

**For Gradle (build.gradle):**
```groovy
runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'
```

**For Gradle (build.gradle.kts):**
```kotlin
runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
```

> **Note:** Remove this dependency once the migration is complete.

### 4. Update javax to jakarta packages

One of the most significant changes in Spring Boot 3 is the migration from `javax.*` to `jakarta.*` packages. Update all imports in your code:

**Common package changes:**
- `javax.servlet` → `jakarta.servlet`
- `javax.persistence` → `jakarta.persistence` 
- `javax.validation` → `jakarta.validation`
- `javax.annotation` → `jakarta.annotation`
- `javax.transaction` → `jakarta.transaction`

You can use tools to help with this migration:
- [OpenRewrite recipes](https://docs.openrewrite.org/recipes/java/migrate/jakarta/javaxmigrationtojakarta)
- [Spring Boot Migrator project](https://github.com/spring-projects-experimental/spring-boot-migrator)
- IDE support (like IntelliJ IDEA)

### 5. Review Configuration Properties

Based on the migration guide, update your application properties. Some notable changes:

- Configuration properties have been renamed - check diagnostic output from the properties-migrator
- Trailing slash matching disabled by default in Spring MVC
- Default date format for logging changed to ISO-8601
- Configuration for `@ConfigurationProperties` classes has changed:
  - Remove `@ConstructorBinding` at the type level
  - Add `@Autowired` if you have dependencies in constructors

### 6. Update Spring MVC/WebFlux Configuration (if applicable)

If you need to restore trailing slash matching behavior:

```java
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
```

For WebFlux:

```java
@Configuration
public class WebConfiguration implements WebFluxConfigurer {
    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
```

### 7. Update Actuator Configuration (if applicable)

If using Spring Boot Actuator, be aware of these changes:

- By default, only the health endpoint is exposed over JMX
- The `httptrace` endpoint has been renamed to `httpexchanges`
- Actuator JSON responses now use an isolated `ObjectMapper` instance
- `/env` and `/configprops` endpoints mask all values by default

### 8. Update Data Access Code (if applicable)

- Hibernate 6.1 is now used - review the [Hibernate migration guides](https://docs.jboss.org/hibernate/orm/6.1/migration-guide/migration-guide.html)
- Redis properties moved from `spring.redis.` to `spring.data.redis.`
- Cassandra properties moved from `spring.data.cassandra.` to `spring.cassandra.`
- Review other data-related changes in the migration guide if applicable

### 9. Update Security Configuration (if applicable)

If using Spring Security, review the [Spring Security 6.0 migration guide](https://docs.spring.io/spring-security/reference/migration/index.html) as there are significant changes:

- Authorization is now applied to every dispatch type
- SAML2 configuration properties have changed
- `ReactiveUserDetailsService` auto-configuration behavior has changed

### 10. Update Testing Code

- If using embedded MongoDB for testing, switch to the auto-configuration library from Flapdoodle or use Testcontainers

### 11. Build and Test

After making the above changes, build and test your application to identify any remaining issues:

```bash
# For Maven
./mvnw clean package

# For Gradle
./gradlew clean build
```

### 12. Remove Properties Migrator

Once the migration is complete, remove the `spring-boot-properties-migrator` dependency.

## Additional Considerations

### Third-Party Libraries

Ensure all third-party libraries are compatible with Spring Boot 3.0 and Jakarta EE. You may need to update these dependencies to their latest versions.

### Update Image Banners

Support for image-based application banners has been removed. Replace any `banner.gif`, `banner.jpg`, or `banner.png` with a text-based `banner.txt` file.

### Auto-configuration Changes

Auto-configuration files have moved from `spring.factories` to `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. Update any custom auto-configuration accordingly.

## Common Migration Issues

1. **Jakarta EE Import Issues**: Most compile-time errors will be related to the `javax` to `jakarta` package migration.

2. **Spring Security Configuration**: Spring Security 6 introduces significant changes that might require rewriting security configurations.

3. **Deprecated API Usage**: Code that was using deprecated APIs in Spring Boot 2.x will now fail to compile.

4. **Database Connectivity**: Make sure JDBC drivers and database clients are updated to versions compatible with Spring Boot 3.

5. **Validation Annotations**: If you're using validation annotations, ensure they're imported from the `jakarta.validation` package.

By following this guide, you should be able to successfully migrate your Spring Boot 2.x application to Spring Boot 3.0.