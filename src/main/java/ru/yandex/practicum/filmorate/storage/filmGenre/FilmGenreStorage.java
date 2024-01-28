package ru.yandex.practicum.filmorate.storage.filmGenre;

public interface FilmGenreStorage {
    public void add(Integer film_id, Integer genre_id);

    public void delete(Integer film_id, Integer genre_id);
}
