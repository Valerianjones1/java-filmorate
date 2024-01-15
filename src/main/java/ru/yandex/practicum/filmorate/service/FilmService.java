package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getStorage() {
        return filmStorage;
    }

    public Film like(Integer filmId, Integer userId) {
        Film foundFilm = filmStorage.get(filmId);
        User foundUser = userStorage.get(userId);
        if (foundFilm != null && foundUser != null) {
            foundFilm.getUsersLikes().add(userId);
        } else {
            if (foundFilm == null && foundUser == null) {
                throw new NotFoundException(String.format("Пользователь с id %s и фильм c id %s не найдены", userId, filmId));
            }
            if (foundFilm == null) {
                throw new FilmNotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
            } else {
                throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
            }
        }
        return foundFilm;
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film foundFilm = filmStorage.get(filmId);
        User foundUser = userStorage.get(userId);

        if (foundFilm != null && foundUser != null) {
            foundFilm.getUsersLikes().remove(userId);
        } else {
            if (foundFilm == null && foundUser == null) {
                throw new NotFoundException(String.format("Пользователь с id %s и фильм c id %s не найдены", userId, filmId));
            }
            if (foundFilm == null) {
                throw new FilmNotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
            } else {
                throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
            }
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesSize).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
