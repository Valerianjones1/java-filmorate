package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    private Integer getIdCounter() {
        return idCounter++;
    }

    @Override
    public Film add(Film film) {
        film.setUsersLikes(new HashSet<>());
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() != null) {
            if (!films.containsKey(film.getId())) {
                log.warn("Такого фильма нет в библиотеке");
                throw new UserNotFoundException(String.format("Фильм с идентификатором %s не найден", film.getId()));
            }
            if (film.getUsersLikes() == null) {
                film.setUsersLikes(new HashSet<>());
            }
            films.put(film.getId(), film);
            log.debug("Обновили фильм {}", film);
        } else {
            log.warn("Идентификатор фильма равен null");
            throw new ValidationException("Идентификатор фильма равен null");
        }
        return film;
    }

    @Override
    public void remove(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %s не найден", filmId));
        }
        films.remove(filmId);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %s не найден", filmId));
        }
        return films.get(filmId);
    }
}
