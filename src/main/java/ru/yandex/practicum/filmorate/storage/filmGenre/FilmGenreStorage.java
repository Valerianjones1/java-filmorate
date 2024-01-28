package ru.yandex.practicum.filmorate.storage.filmGenre;

public interface FilmGenreStorage {
    public void add(Integer filmId, Integer genreId);

    public void delete(Integer filmId, Integer genreId);
}
