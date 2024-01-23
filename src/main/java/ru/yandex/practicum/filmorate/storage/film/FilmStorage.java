package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public void remove(Integer filmId);

    public List<Film> findAll();

    public List<Film> findAllByPopular(Integer count);

    public Film get(Integer filmId);

    public Film like(Film film, User user);

    public void removeLike(Film film, User user);
}
