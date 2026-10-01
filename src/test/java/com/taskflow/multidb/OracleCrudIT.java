package com.taskflow.multidb;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

/**
 * Misma suite de tests que Postgres/MySQL, pero contra Oracle Free real via Testcontainers.
 * Deshabilitado por defecto: la imagen gvenzl/oracle-free pesa varios GB y el primer arranque
 * (creacion de datafiles) puede tardar 3-5 minutos, demasiado para un feedback loop normal.
 * Para habilitarla: borrar el @Disabled y correr `mvn test -Dtest=OracleCrudIT`.
 */
@Testcontainers
@Disabled("Requiere descargar gvenzl/oracle-free (varios GB, arranque lento). Ver comentario de la clase para habilitarla.")
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
