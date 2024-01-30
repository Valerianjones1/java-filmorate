package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreStorage;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreStorage = genreRepository;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre getGenre(Integer genreId) {
        Genre genre = genreStorage.get(genreId);
        if (genre == null) {
            throw new NotFoundException(String.format("Жанр %s не был найден", genreId));
        }
        return genre;
    }

}
