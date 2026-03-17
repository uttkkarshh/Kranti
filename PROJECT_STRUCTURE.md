# Kranti - Project Structure

This document describes the layout, key files, build/run instructions, and useful notes for the Kranti Spring Boot project.

Project root
- .classpath, .project, .settings/         : IDE metadata (Eclipse)
- .mvn/, mvnw, mvnw.cmd                     : Maven wrapper
- pom.xml                                   : Maven project descriptor (Java 17, Spring Boot 3.3.5)
- HELP.md                                   : Project helper notes
- src/                                      : Source code
- target/                                   : Build output (generated)

Top-level Maven coordinates (from pom.xml)
- groupId: com.ut
- artifactId: Kranti
- version: 0.0.1-SNAPSHOT
- Java version: 17
- Spring Boot parent: 3.3.5

Dependencies of note
- spring-boot-starter-web, webflux: web and REST support
- spring-boot-starter-data-jpa: JPA/Hibernate
- spring-boot-starter-security: Spring Security
- spring-boot-starter-websocket: WebSocket support
- mysql-connector-j: MySQL runtime driver
- jjwt (io.jsonwebtoken): JWT token support
- modelmapper: DTO mapping
- hibernate-spatial: spatial queries (used by events nearby search)

Directory layout (src/main)
- src/main/java/com/ut/kranti
  - KrantiApplication.java        : Spring Boot entry point
  - auth/                        : Security and JWT handling
    - SecurityConfig.java        : Security configuration (filter chain, CORS)
    - JwtFilter.java             : JWT authentication filter
    - JWTService.java            : JWT helper service
    - CustomUserDetailsService.java, UserPrincipal.java  : user details handling
  - user/                        : User domain
    - UserController.java        : REST endpoints for users (login, register, CRUD, follow)
    - UserService.java, UserRepository.java
    - UserDto.java, UserMapper.java, UserProfile.java
    - post/                      : Post related domain (Post, PostService, PostController, PostDto, Like)
  - event/                       : Event domain
    - EventController.java       : REST for events (create, read, update, delete, nearby search)
    - EventService.java, EventRepository.java
    - EventDTO.java, EventMapper.java
  - comments/                    : Comments domain (Comment, CommentService, CommentController)
  - follower/                    : Follow/follower domain (Follower, FollowRequest, controllers and services)
  - exception/                   : Custom exceptions and global handler

- src/main/resources
  - application.properties       : Configuration (server.port, datasource, JPA settings)
  - static/, templates/          : Static assets and templates (if used)

- src/test/java/com/ut/kranti
  - KrantiApplicationTests.java  : Basic unit/integration test scaffold

Build and run
1. Build (uses Maven wrapper included)
   - Windows (cmd): mvnw.cmd clean package
   - Or with installed Maven: mvn clean package

2. Run
   - From target (after package): java -jar target\Kranti-0.0.1-SNAPSHOT.jar
   - Or with Maven: mvn spring-boot:run

Configuration
- Default port: 8080 (override via SERVER_PORT env var)
- Database: MySQL - configured via environment variables or defaults in application.properties
  - MYSQL_HOST (default: localhost)
  - MYSQL_PORT (default: 3306)
  - MYSQL_DB   (default: kranti)
  - MYSQL_USER (default: root)
  - MYSQL_PASSWORD (default: root)
- JPA ddl-auto default: update (DDL_AUTO env var)

Key REST endpoints (representative)
- Users
  - POST /api/users/login                : login
  - POST /api/users/register             : register a new user
  - GET /api/users/{id}                  : get user by id
  - GET /api/users/allusers              : list all users
  - POST /api/users/{id}/follow?followerId=... : follow a user

- Events
  - POST /api/events/create              : create event
  - GET /api/events                      : list events
  - GET /api/events/{id}                 : get event by id
  - GET /api/events/nearby?latitude=...&longitude=...&radius=... : find events near location

Security
- Spring Security is configured via `SecurityConfig`. The filter chain registers a `JwtFilter` and sets session management to stateless.
- CORS allows localhost:3000 and a specific netlify app.

Notes and next steps
- The project uses JWT; ensure environment properties for secrets (if not yet present) are set before running.
- The project includes spatial/haversine-style location search using `hibernate-spatial` — ensure the database and dialect support spatial functions.
- Consider adding README.md with quickstart and environment variables list (this PROJECT_STRUCTURE.md is a first-pass document).

Contact
- If you want, I can also generate a README.md with runnable examples, Dockerfile, or a small script to seed the DB with test data.
