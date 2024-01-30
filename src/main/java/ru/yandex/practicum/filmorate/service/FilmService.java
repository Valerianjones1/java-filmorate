package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    public Film add(Film film) {
        return filmRepository.add(film);
    }

    public void remove(Integer filmId) {
        filmRepository.remove(filmId);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Идентификатор фильма равен null");
            throw new ValidationException("Идентификатор фильма равен null");
        }
        if (filmRepository.get(film.getId()) == null) {
            log.warn(String.format("Фильм с идентификатором %s не найден", film));
            throw new NotFoundException(String.format("Фильм с идентификатором %s не найден", film.getId()));
        }

        filmRepository.update(film);
        log.debug("Обновили фильм {}", film);
        return film;
    }

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film get(Integer filmId) {
        Film film = filmRepository.get(filmId);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        }
        return film;
    }

    public Film like(Integer filmId, Integer userId) {
        Film film = filmRepository.get(filmId);
        User user = userRepository.get(userId);
        Film likedFilm;

        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        } else if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        likedFilm = filmRepository.like(film, user);
        return likedFilm;
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmRepository.get(filmId);
        User user = userRepository.get(userId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм c идентификатором %s не найден", filmId));
        } else if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }

        filmRepository.removeLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findAllByPopular(count);
    }
}
