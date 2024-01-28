package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void add(Integer filmId, Integer genreId) {
        String sqlQuery = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public void delete(Integer filmId, Integer genreId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id=? and genre_id=?";
        jdbcTemplate.update(sqlQuery, filmId, genreId);

    }
}
