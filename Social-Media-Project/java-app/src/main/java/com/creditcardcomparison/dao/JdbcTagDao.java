package com.creditcardcomparison.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcTagDao implements TagDao {

    private final JdbcTemplate jdbcTemplate;

    //Constructor
    public JdbcTagDao(DataSource dataSource) throws CannotGetJdbcConnectionException, DaoException, DataIntegrityViolationException {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<String> getTags() {
        List<String> topics = new ArrayList<>();

        String sql = """
            SELECT name FROM tag;
            """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            String topic = results.getString("name");
            topics.add(topic);
        }

        return topics;
    }
}
