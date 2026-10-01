package com.taskflow.multidb;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Misma suite de tests que Postgres/MySQL, pero contra SQL Server real via Testcontainers.
 * Deshabilitado por defecto: la imagen de SQL Server es pesada (~1.5GB) y lenta de descargar
 * en un runner sin la imagen ya cacheada. Para habilitarla: borrar el @Disabled y correr
 * `mvn test -Dtest=SqlServerCrudIT`. Requiere Docker con al menos 4GB de RAM disponibles.
 */
@Testcontainers
@Disabled("Requiere descargar mcr.microsoft.com/mssql/server (pesada). Ver comentario de la clase para habilitarla.")
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
