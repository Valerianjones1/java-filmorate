package ru.yandex.practicum.filmorate.repository.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
@Slf4j
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcFilmRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

        List<Genre> genres = new ArrayList<>(film.getGenres());
        if (!genres.isEmpty()) {
            addGenresToFilm(film.getId(), genres);
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

        // Удаление старых жанров
        String sqlQuery2 = "DELETE FROM film_genre WHERE film_id = :film_id";
        SqlParameterSource params = new MapSqlParameterSource("film_id", film.getId());
        jdbcTemplate.update(sqlQuery2, params);

        // Добавление новых жанров
        List<Genre> newGenres = new ArrayList<>(film.getGenres());
        addGenresToFilm(film.getId(), newGenres);

        film.setGenres(film.getGenres());
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
        String sqlQuery = "SELECT film.id as id, mpa.name as mpa_name,mpa.id as mpa_id," +
                "g.name as genre_name, g.id as genre_id, * " +
                "FROM film  " +
                "LEFT JOIN mpa ON mpa.id=film.mpa_id " +
                "LEFT JOIN film_genre as fg ON fg.film_id=film.id " +
                "LEFT JOIN genre as g ON g.id=fg.genre_id";

        return findAllFilms(sqlQuery);
    }

    @Override
    public List<Film> findAllByPopular(Integer count) {
        String sqlQuery = "SELECT f.id as id, m.name as mpa_name,m.id as mpa_id," +
                "g.name as genre_name, g.id as genre_id, * " +
                "FROM film as f " +
                "LEFT JOIN film_like as fl ON fl.film_id=f.id " +
                "LEFT JOIN mpa as m ON m.id=f.mpa_id " +
                "LEFT JOIN film_genre as fg ON fg.film_id=f.id " +
                "LEFT JOIN genre as g ON g.id=fg.genre_id " +
                "GROUP BY fl.user_id, f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT :limit";

        SqlParameterSource params = new MapSqlParameterSource("limit", count);

        return findAllPopularFilmFilms(sqlQuery, params);
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

        SqlParameterSource params = new MapSqlParameterSource("id", filmId);
        List<Film> films = jdbcTemplate.query(sqlQuery, params, this::makeFilm);
        return films.isEmpty() ? null : films.get(0);

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

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = rsToFilm(rs);
        if (rs.getInt("genre_id") != 0) {
            do {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
            } while (rs.next());
        }
        return film;
    }

    private List<Film> findAllFilms(String sqlQuery) {
        HashMap<Integer, Film> films = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            int filmId = rs.getInt("id");
            if (!films.containsKey(filmId)) {
                Film film = rsToFilm(rs);
                films.put(filmId, film);
            }
            if (rs.getInt("genre_id") != 0) {
                films.get(filmId).getGenres().add(new Genre(rs.getInt("genre_id"),
                        rs.getString("genre_name")));
            }
            return null;
        });
        return new ArrayList<>(films.values());
    }

    private List<Film> findAllPopularFilmFilms(String sqlQuery, SqlParameterSource params) {
        HashMap<Integer, Film> films = new HashMap<>();
        jdbcTemplate.query(sqlQuery, params, (rs, rowNum) -> {
            int filmId = rs.getInt("id");
            if (!films.containsKey(filmId)) {
                Film film = rsToFilm(rs);
                films.put(filmId, film);
            }
            if (rs.getInt("genre_id") != 0) {
                films.get(filmId).getGenres().add(new Genre(rs.getInt("genre_id"),
                        rs.getString("genre_name")));
            }
            return null;
        });
        return new ArrayList<>(films.values());
    }

    private Film rsToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        if (rs.getInt("mpa_id") != 0) {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
        }
        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
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

    private void addGenresToFilm(Integer filmId, List<Genre> genres) {
        Map<String, Object>[] filmGenres = new HashMap[genres.size()];
        int count = 0;
        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            filmGenres[count++] = map;
        }
        jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE(film_id, genre_id) VALUES(:film_id, :genre_id)", filmGenres);
    }
}
