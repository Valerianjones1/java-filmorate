package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre getGenre(Integer genreId) {
        Genre foundGenre = genreStorage.get(genreId);
        if (foundGenre == null) {
            throw new NotFoundException(String.format("Жанр %s не был найден", genreId));
        }
        return foundGenre;
    }

}