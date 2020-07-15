package com.heidelberg;
/**
 * Maps record from work list database table to {@link com.heidelberg.WorkListEntry} POJO
 */

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkListEntryRowMapper implements RowMapper<WorkListEntry> {
    @Override
    public WorkListEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        WorkListEntry entry = new WorkListEntry();
        entry.setFilename(rs.getString("FileId"));
        entry.setId(rs.getString("Id"));
        return entry;
    }
}
