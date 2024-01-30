package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {
    public List<Genre> findAll();

    public Genre get(Integer genreId);
}
