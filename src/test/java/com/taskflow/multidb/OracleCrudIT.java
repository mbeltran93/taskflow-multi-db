package com.taskflow.multidb;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

/**
 * Misma suite de tests que Postgres/MySQL, pero contra Oracle Free real via Testcontainers.
 * Deshabilitado por defecto por el mismo motivo que SqlServerCrudIT (ver ese comentario):
 * en esta maquina Testcontainers no logra hablar con el daemon de Docker Desktop para
 * Windows (docker-java recibe una respuesta "stub" del named pipe), no por el peso de la
 * imagen. El CRUD completo contra Oracle real SI esta verificado, por fuera de
 * Testcontainers (docker compose + curl, ver README). Para habilitarla: borrar el
 * @Disabled y correr `mvn verify -Dit.test=OracleCrudIT` en una maquina sin este problema.
 */
@Testcontainers
@Disabled("Testcontainers no puede hablar con el daemon de Docker Desktop en esta maquina (ver comentario de la clase). El CRUD contra Oracle real SI esta verificado por fuera de Testcontainers, ver README.")
class OracleCrudIT extends AbstractCrudIT {

    @Container
    static final OracleContainer ORACLE =
            new OracleContainer("gvenzl/oracle-free:23-slim")
                    .withUsername("taskflow")
                    .withPassword("taskflow");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ORACLE::getJdbcUrl);
        registry.add("spring.datasource.username", ORACLE::getUsername);
        registry.add("spring.datasource.password", ORACLE::getPassword);
        registry.add("spring.datasource.driver-class-name", ORACLE::getDriverClassName);
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.OracleDialect");
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/oracle/changelog-master.yaml");
    }
}
