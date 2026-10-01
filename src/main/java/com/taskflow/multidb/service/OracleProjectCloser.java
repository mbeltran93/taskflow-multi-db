package com.taskflow.multidb.service;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

/**
 * Llama al stored procedure PL/SQL close_project (ver
 * db/changelog/oracle/changelog-master.yaml, changeset 2-close-project-procedure).
 * El procedure hace el UPDATE masivo del lado de la base y devuelve la
 * cantidad de filas afectadas por un parametro OUT, asi que aca no hay
 * logica de negocio: solo el binding JDBC con SimpleJdbcCall.
 */
@Service
@Profile("oracle")
public class OracleProjectCloser implements ProjectCloser {

    private static final String PARAM_PROJECT_ID = "P_PROJECT_ID";
    private static final String PARAM_UPDATED_COUNT = "P_UPDATED_COUNT";

    private final SimpleJdbcCall jdbcCall;

    public OracleProjectCloser(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("close_project")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(PARAM_PROJECT_ID, Types.NUMERIC),
                        new SqlOutParameter(PARAM_UPDATED_COUNT, Types.NUMERIC)
                );
    }

    @Override
    public int closeProject(Long projectId) {
        Map<String, Object> result = jdbcCall.execute(
                new MapSqlParameterSource().addValue(PARAM_PROJECT_ID, projectId));
        Number updatedCount = (Number) result.get(PARAM_UPDATED_COUNT);
        return updatedCount == null ? 0 : updatedCount.intValue();
    }
}
