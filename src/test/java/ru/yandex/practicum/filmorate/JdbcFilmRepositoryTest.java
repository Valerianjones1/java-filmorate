package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcFilmRepositoryTest {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        Film film = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);

        Film newFilm = filmStorage.add(film);

        List<Film> films = filmStorage.findAll();
        Film foundFilm = filmStorage.get(newFilm.getId());

        assertThat(foundFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);

        assertThat(films)
                .isNotEmpty() // проверяем, что объект не равен null
                .contains(newFilm);
    }

    @Test
    public void testFindAll() {
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);
        List<Film> emptyFilms = filmStorage.findAll();

        Film film = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));

        Film newFilm = filmStorage.add(film);

        List<Film> filledFilms = filmStorage.findAll();

        assertThat(filledFilms)
                .isNotEqualTo(emptyFilms)
                .contains(newFilm);

        assertThat(emptyFilms.size())
                .isNotEqualTo(filledFilms.size())
                .isEqualTo(0);
    }

    @Test
    public void testUpdate() {
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);

        Film film = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));

        Film newFilm = filmStorage.add(film);

        Film foundFilm = filmStorage.get(newFilm.getId());


        Film updatedFilm = filmStorage.update(new Film(newFilm.getId(), "Тест Обнов фильм", "Обнов Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G")));

        Film foundUpdFilm = filmStorage.get(updatedFilm.getId());

        assertThat(foundUpdFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isNotEqualTo(foundFilm);

    }

    @Test
    public void testRemove() {
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);

        Film film = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));

        Film newFilm = filmStorage.add(film);

        Film foundFilm = filmStorage.get(newFilm.getId());


        filmStorage.remove(foundFilm.getId());

        Film removedFilm = filmStorage.get(foundFilm.getId());

        assertThat(removedFilm)
                .isNull(); // проверяем, что объект не равен null// проверяем, что значения полей нового

    }

    @Test
    public void testFindAllPopular() {
        UserRepository userRepository = new JdbcUserRepository(jdbcTemplate);
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);

        Film film1 = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));

        Film film2 = new Film("Тест фиzxcльм", "Опrисание",
                LocalDate.of(2006, 11, 12), 100, new Mpa(1, "G"));

        Film newFilm = filmStorage.add(film1);
        User newUser = new User("user@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User adddedUser = userRepository.add(newUser);


        filmStorage.add(film2);

        filmStorage.like(newFilm, adddedUser);

        List<Film> popularFilms = filmStorage.findAllByPopular(10);

        assertThat(popularFilms)
                .isNotEmpty();

        assertThat(popularFilms.get(0))
                .isEqualTo(newFilm);
        assertThat(popularFilms.size())
                .isEqualTo(2);

    }

    @Test
    public void testRemoveLike() {
        UserRepository userRepository = new JdbcUserRepository(jdbcTemplate);
        JdbcFilmRepository filmStorage = new JdbcFilmRepository(jdbcTemplate);

        Film film1 = new Film("Тест фильм", "Описание",
                LocalDate.of(2005, 11, 12), 100, new Mpa(1, "G"));

        Film newFilm = filmStorage.add(film1);
        User newUser = new User("user@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User addedUser = userRepository.add(newUser);

        List<Map<Integer, Integer>> emptyLikes = getFilmLikes(newFilm.getId(), addedUser.getId());

        filmStorage.like(newFilm, addedUser);

        List<Map<Integer, Integer>> filledLikes = getFilmLikes(newFilm.getId(), addedUser.getId());


        assertThat(emptyLikes.isEmpty()).isTrue();
        assertThat(filledLikes.isEmpty()).isFalse();
    }

    private List<Map<Integer, Integer>> getFilmLikes(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT film_id, user_id FROM film_like WHERE film_id = :film_id and user_id = :user_id";
        SqlParameterSource params = new MapSqlParameterSource(Map.of("film_id", filmId, "user_id", userId));

        return jdbcTemplate.query(sqlQuery, params, (rs, rowNum) ->
                Map.of(rs.getInt("film_id"), rs.getInt("user_id")));
    }
}