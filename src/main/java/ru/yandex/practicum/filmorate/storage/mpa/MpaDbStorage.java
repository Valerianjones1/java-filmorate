package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Autowired
    public MpaDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    @Override
    public Mpa get(Integer mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", mpaId);
        try {
            return jdbcTemplate.queryForObject(sqlQuery, params, this::makeMpa);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
