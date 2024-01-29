package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void add(Integer filmId, Integer genreId) {
        String sqlQuery = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES(:film_id, :genre_id)";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", filmId, "genre_id", genreId));
        jdbcTemplate.update(sqlQuery, params);
    }

    @Override
    public void delete(Integer filmId, Integer genreId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = :film_id and genre_id = :genre_id";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", filmId, "genre_id", genreId));
        jdbcTemplate.update(sqlQuery, params);

    }
}
