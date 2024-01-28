package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genre ORDER BY ID";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::makeGenre);
        return genres;
    }

    @Override
    public Genre get(Integer genreId) {
        String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
