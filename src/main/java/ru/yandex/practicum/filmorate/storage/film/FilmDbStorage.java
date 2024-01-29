package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmDbStorage(NamedParameterJdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public Film add(Film film) {
        String sqlQuery1 = "INSERT INTO film(name, description, release_date, duration, mpa_id, rate) " +
                "VALUES (:name, :description, :release_date, :duration, :mpa_id, :rate)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource filmParams = new MapSqlParameterSource(filmToMap(film));

        jdbcTemplate.update(sqlQuery1, filmParams, keyHolder);

        Integer filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        film.setId(filmId);

        Set<Genre> filmGenres = film.getGenres();
        if (filmGenres != null && !filmGenres.isEmpty()) {
            String sqlQuery2 = "INSERT INTO film_genre(film_id, genre_id) " +
                    "VALUES (:film_id, :genre_id)";

            for (Genre genre : filmGenres) {
                SqlParameterSource filmGenreParams = new MapSqlParameterSource(Map.of("film_id", film.getId(),
                        "genre_id", genre.getId()));
                jdbcTemplate.update(sqlQuery2, filmGenreParams);
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE film SET " +
                "name = :name, description = :description, release_date = :release_date, " +
                "duration = :duration, rate = :rate, mpa_id = :mpa_id " +
                "WHERE id = :id";

        SqlParameterSource filmParams = new MapSqlParameterSource(filmToMap(film));

        jdbcTemplate.update(sqlQuery, filmParams);

        LinkedHashSet<Genre> oldGenres = get(film.getId()).getGenres();
        LinkedHashSet<Genre> newGenres = film.getGenres();

        for (Genre genre : oldGenres) {
            filmGenreStorage.delete(film.getId(), genre.getId());
        }

        for (Genre genre : newGenres) {
            filmGenreStorage.add(film.getId(), genre.getId());
        }

        film.setGenres(newGenres);
        return film;
    }

    @Override
    public void remove(Integer filmId) {
        String sqlQuery = "DELETE FROM film WHERE id = :id";
        SqlParameterSource filmParams = new MapSqlParameterSource("id", filmId);
        jdbcTemplate.update(sqlQuery, filmParams);
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT film.id as id, mpa.name as mpa_name,mpa.id as mpa_id, * " +
                "FROM film  " +
                "LEFT JOIN mpa ON mpa.id=film.mpa_id ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);

        for (Film film : films) {
            List<Genre> genres = findGenreOfFilm(film.getId());
            film.getGenres().addAll(genres);
        }
        return films;
    }

    @Override
    public List<Film> findAllByPopular(Integer count) {
        String sqlQuery = "SELECT m.name as mpa_name, * " +
                "FROM film as f " +
                "LEFT JOIN film_like as fl ON fl.film_id=f.id " +
                "LEFT JOIN mpa as m ON m.id=f.mpa_id " +
                "GROUP BY fl.user_id, f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT :limit";

        SqlParameterSource params = new MapSqlParameterSource("limit", count);
        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, params, this::makeFilm);
        for (Film film : popularFilms) {
            List<Genre> genres = findGenreOfFilm(film.getId());
            film.getGenres().addAll(genres);
        }

        return popularFilms;
    }

    @Override
    public Film get(Integer filmId) {
        String sqlQuery = "SELECT film.id as id, mpa.name as mpa_name,mpa.id as mpa_id, " +
                "g.name as genre_name, g.id as genre_id, * " +
                "FROM film  " +
                "LEFT JOIN mpa ON mpa.id=film.mpa_id " +
                "LEFT JOIN film_genre as fg ON fg.film_id = film.id " +
                "LEFT JOIN genre as g ON g.id=fg.genre_id " +
                "WHERE film.id = :id";
        try {
            SqlParameterSource params = new MapSqlParameterSource("id", filmId);
            return jdbcTemplate.queryForObject(sqlQuery, params, this::makeFilmForGet);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film like(Film film, User user) {
        String sqlQuery = "MERGE INTO FILM_LIKE as fl1\n" +
                "USING (VALUES (:film_id,:user_id)) as fl2 (film_id, user_id)\n" +
                "ON fl1.FILM_ID=fl2.film_id and fl1.USER_ID=fl2.user_id\n" +
                "WHEN MATCHED THEN UPDATE SET fl1.FILM_ID=fl2.film_id and fl1.USER_ID=fl2.user_id\n" +
                "WHEN NOT MATCHED THEN INSERT (film_id, user_id) VALUES (fl2.film_id, fl2.user_id)";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", film.getId(), "user_id", user.getId()));
        jdbcTemplate.update(sqlQuery, params);
        return film;
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "DELETE FROM film_like WHERE film_id = :film_id and user_id = :user_id";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", film.getId(), "user_id", user.getId()));
        jdbcTemplate.update(sqlQuery, params);
    }

    private List<Genre> findGenreOfFilm(Integer filmId) {
        String sqlQuery = "SELECT g.name as genre_name, g.id as genre_id FROM film as f " +
                "JOIN film_genre as fg ON fg.film_id = f.id " +
                "JOIN genre as g ON g.id=fg.genre_id " +
                "WHERE f.id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", filmId);
        return jdbcTemplate.query(sqlQuery, params, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        });
    }

    public List<Map<Integer, Integer>> getFilmLikes(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT film_id, user_id FROM film_like WHERE film_id = :film_id and user_id = :user_id";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", filmId, "user_id", userId));

        return jdbcTemplate.query(sqlQuery, params, (rs, rowNum) ->
                Map.of(rs.getInt("film_id"), rs.getInt("user_id")));
    }

    private Film makeFilmForGet(ResultSet rs, int rowNum) throws SQLException {
        Film film = makeFilm(rs, rowNum);

        if (rs.getInt("genre_id") != 0) {
            do {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
            } while (rs.next());
        }
        return film;
    }

    public Film makeFilm(ResultSet rs, Integer rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null);
        values.put("rate", film.getRate());

        if (film.getId() != null) {
            values.put("id", film.getId());
        }
        return values;
    }
}
