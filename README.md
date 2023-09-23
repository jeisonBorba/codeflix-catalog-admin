<center>
   <p align="center">
      <img align="center" alt="Spring" width="150" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" />
      <img align="center" alt="Spring" width="150" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" />&nbsp;
   </p>
   <h1 align="center">Microservice: Video Catalog Admin with Java and Spring Boot</h1>
   <p align="center">
      Microservice related to the Video Catalog Administrator backend created in the FullCycle Course <br />
      Using Clean Architecture, DDD, TDD, Event Source and more
   </p>
</center>
<br />

## Necessary tools

- JDK 17
- IDE of your preference (Intellij, Eclipse, VSCode)
- Docker

## How to run the project locally?
> The project contains a self-contained Sanbox directory which will facilitate the process

1. Cloning the repository:
```sh
git clone https://github.com/jeisonBorba/codeflix-catalog-admin.git
```

2. Running the necessary services (MySQL, RabbitMQ, Keycloak) on Docker

Navigate to the sanbox/services directory and run the following command:
```shell
docker-compose up -d
```

3. Configuring the MySQL database with Flyway

In the project root directory, run the following command:
```shell
./gradlew flywayMigrate
```

More details about [Flyway Gradle Plugin](https://flywaydb.org/documentation/usage/gradle/info)

4. Configuring the app authentication with Keycloak
   - Open the Keycloak application accessing http://localhost:8433;
   - Create a new realm named 'codeflix';
   - Create a new client name 'catalog-admin' and enable the Client authentication and Service account roles properties;
   - Create the 'catalog_admin', 'catalog_categories', 'catalog_genres', 'catalog_cast_members' and 'catalog_videos' roles;
   - Create new groups;
   - Create new users and link them to one or more groups.

4. (OPTIONAL) Running the application on Docker

Navigate to the sanbox/app directory and run the following command:
```shell
docker-compose up -d
```

5. (OPTIONAL) Running ELK

Navigate to the sanbox/elk directory and run the following command:
```shell
docker-compose up -d
```

> Important: create a .env file in the project root directory using the .env.example file as example and replace the values as needed

6. (OPTIONAL) Running the application via IDE
> Run as a Java application via the main() method in the Main.java class

7. (OPTIONAL) Running the application via command line

In the project root directory, run the following command:
```shell
./gradlew bootRun
```