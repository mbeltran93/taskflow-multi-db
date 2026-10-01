package com.taskflow.multidb;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Misma suite de tests que Postgres/MySQL, pero contra SQL Server real via Testcontainers.
 * Deshabilitado por defecto: no es por el peso de la imagen (ya la tenemos descargada y
 * corriendo bien via docker-compose, ver README) sino porque en esta maquina Testcontainers
 * no logra hablar con el daemon de Docker Desktop para Windows -- el named pipe le devuelve
 * a docker-java (el cliente que usa Testcontainers, distinto del docker.exe/CLI oficial) una
 * respuesta "stub" (un docker info vacio con el label com.docker.desktop.address=npipe://./pipe/docker_cli),
 * asi que DockerClientFactory nunca encuentra un Docker environment valido. Confirmado otra vez
 * el 2026-10-01, incluso probando con DOCKER_HOST apuntando directo al pipe del engine
 * (dockerDesktopLinuxEngine): mismo resultado. No tiene que ver con espacio en disco ni con
 * esta clase de test: el CRUD completo contra SQL Server real SI esta verificado, pero por
 * fuera de Testcontainers (docker compose + curl, ver README). Para habilitarla: borrar el
 * @Disabled y correr `mvn verify -Dit.test=SqlServerCrudIT` en una maquina donde Testcontainers
 * si pueda hablar con el daemon (Linux, Mac, CI, o Docker Desktop con WSL2 y el socket expuesto
 * directo a la distro).
 */
@Testcontainers
@Disabled("Testcontainers no puede hablar con el daemon de Docker Desktop en esta maquina (ver comentario de la clase). El CRUD contra SQL Server real SI esta verificado por fuera de Testcontainers, ver README.")
class SqlServerCrudIT extends AbstractCrudIT {

    @Container
    static final MSSQLServerContainer<?> SQLSERVER =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                    .acceptLicense();

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQLSERVER::getJdbcUrl);
        registry.add("spring.datasource.username", SQLSERVER::getUsername);
        registry.add("spring.datasource.password", SQLSERVER::getPassword);
        registry.add("spring.datasource.driver-class-name", SQLSERVER::getDriverClassName);
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/sqlserver/changelog-master.yaml");
    }
}
