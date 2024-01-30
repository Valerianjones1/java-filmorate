package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcGenreRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genre ORDER BY ID";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre get(Integer genreId) {
        String sqlQuery = "SELECT * FROM genre WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", genreId);

        List<Genre> genres = jdbcTemplate.query(sqlQuery, params, this::makeGenre);
        return genres.isEmpty() ? null : genres.get(0);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
