package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert insertFilm = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");

        Integer filmId = insertFilm.executeAndReturnKey(film.toMap()).intValue();

        film.setId(filmId);

        List<Genre> filmGenres = film.getGenres();
        if (filmGenres != null && !filmGenres.isEmpty()) {
            SimpleJdbcInsert insertFilmGenre = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("film_genre")
                    .usingGeneratedKeyColumns("id");
            for (Genre genre : filmGenres) {
                Map<String, Object> params = Map.of("film_id", film.getId(), "genre_id", genre.getId());
                insertFilmGenre.executeAndReturnKey(params).intValue();
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " +
                "WHERE id = ?";

        Film oldFilm = get(film.getId());
        Set<Genre> uniqueGenres = new LinkedHashSet<>(film.getGenres());

        for (Genre genre : uniqueGenres) {
            List<Integer> oldGenres = oldFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            if (!oldGenres.contains(genre.getId())) {
                filmGenreStorage.add(film.getId(), genre.getId());
            }
        }
        for (Genre genre : oldFilm.getGenres()) {
            List<Integer> newGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            if (!newGenres.contains(genre.getId())) {
                filmGenreStorage.delete(film.getId(), genre.getId());
            }
        }
        film.setGenres(new ArrayList<>(uniqueGenres));

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        return film;
    }

    @Override
    public void remove(Integer filmId) {
        String sqlQuery = "DELETE FROM film WHERE id = ?";

        jdbcTemplate.update(sqlQuery, filmId);

    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT film.id as id, mpa.name as mpa_name,mpa.id as mpa_id," +
                "description, film.name, release_date, duration " +
                "FROM film  " +
                "JOIN mpa ON mpa.id=film.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::makeFilmNew);
    }

    @Override
    public List<Film> findAllByPopular(Integer count) {
        String sqlQuery = "SELECT m.name as mpa_name, * " +
                "FROM film as f " +
                "LEFT JOIN film_like as fl ON fl.film_id=f.id " +
                "JOIN mpa as m ON m.id=f.mpa_id " +
                "GROUP BY fl.user_id, f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::makeFilmNew, count);
    }

    @Override
    public Film get(Integer filmId) {
        String sqlQuery = "SELECT film.id as id, mpa.name as mpa_name,mpa.id as mpa_id," +
                "description, film.name, release_date, duration " +
                "FROM film  " +
                "JOIN mpa ON mpa.id=film.mpa_id " +
                "WHERE film.id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilmNew, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film like(Film film, User user) {
        String sqlQuery = "INSERT INTO film_like(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
        return film;
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "DELETE FROM film_like WHERE film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    private List<Genre> findGenreOfFilm(Integer filmId) {
        String sqlQuery = "SELECT g.name as genre_name, g.id as genre_id FROM film as f " +
                "JOIN film_genre as fg ON fg.film_id = f.id " +
                "JOIN genre as g ON g.id=fg.genre_id " +
                "WHERE f.id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }, filmId);
    }

    public List<Map<Integer, Integer>> getFilmLikes(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT film_id, user_id FROM film_like WHERE film_id = ? and user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Map.of(rs.getInt("film_id"), rs.getInt("user_id")), filmId, userId);
    }

    private Film makeFilmNew(ResultSet rs, int rowNum) throws SQLException {
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

        List<Genre> filmGenres = findGenreOfFilm(film.getId());

        for (Genre genre : filmGenres) {
            film.getGenres().add(genre);
        }

        return film;
    }
}
