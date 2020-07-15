package com.heidelberg;
/**
 * Service to fetch list of {@link com.heidelberg.WorkListEntry} from respective database table
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkListService {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<WorkListEntry> rowMapper = new WorkListEntryRowMapper();

    public WorkListService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WorkListEntry> fetchEntries() {
        return jdbcTemplate.query("SELECT TOP 10 * FROM IotMessage", rowMapper);
    }
}
