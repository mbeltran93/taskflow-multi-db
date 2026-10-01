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
 * Llama al stored procedure T-SQL close_project (ver
 * db/changelog/sqlserver/changelog-master.yaml, changeset
 * 2-close-project-procedure). Mismo contrato que la version de Oracle: un
 * parametro de entrada (el id del proyecto) y un OUT con la cantidad de
 * tareas que se marcaron DONE.
 */
@Service
@Profile("sqlserver")
public class SqlServerProjectCloser implements ProjectCloser {

    private static final String PARAM_PROJECT_ID = "ProjectId";
    private static final String PARAM_UPDATED_COUNT = "UpdatedCount";

    private final SimpleJdbcCall jdbcCall;

    public SqlServerProjectCloser(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("close_project")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(PARAM_PROJECT_ID, Types.BIGINT),
                        new SqlOutParameter(PARAM_UPDATED_COUNT, Types.INTEGER)
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
