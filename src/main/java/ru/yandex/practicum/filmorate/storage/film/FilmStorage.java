package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public void remove(Integer filmId);

    public List<Film> findAll();

    public Film get(Integer filmId);
}
