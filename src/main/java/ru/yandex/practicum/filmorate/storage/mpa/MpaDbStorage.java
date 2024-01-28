package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM mpa ORDER BY id ASC";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, this::makeMpa);
        return mpas;
    }

    @Override
    public Mpa get(Integer mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
