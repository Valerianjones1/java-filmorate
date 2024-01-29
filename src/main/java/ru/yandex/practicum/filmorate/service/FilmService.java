package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public void remove(Integer filmId) {
        filmStorage.remove(filmId);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Идентификатор фильма равен null");
            throw new ValidationException("Идентификатор фильма равен null");
        }
        if (filmStorage.get(film.getId()) == null) {
            log.warn(String.format("Фильм с идентификатором %s не найден", film));
            throw new NotFoundException(String.format("Фильм с идентификатором %s не найден", film.getId()));
        }

        filmStorage.update(film);
        log.debug("Обновили фильм {}", film);
        return film;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film get(Integer filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        }
        return film;
    }

    public Film like(Integer filmId, Integer userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        Film likedFilm;

        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        } else if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        likedFilm = filmStorage.like(film, user);
        return likedFilm;
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        } else if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }

        filmStorage.removeLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.findAllByPopular(count);
    }
}
