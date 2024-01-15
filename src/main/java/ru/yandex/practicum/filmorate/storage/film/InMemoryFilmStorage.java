package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmsLikes = new HashMap<>();
    private int idCounter = 1;

    private Integer getIdCounter() {
        return idCounter++;
    }

    @Override
    public Film add(Film film) {
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        filmsLikes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Обновили фильм {}", film);
        return film;
    }

    @Override
    public void remove(Integer filmId) {
        films.remove(filmId);
        filmsLikes.remove(filmId);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(Integer filmId) {
        return films.get(filmId);
    }

    public Film like(Film film, User user) {
        filmsLikes.get(film.getId()).add(user.getId());
        return film;
    }

    public void removeLike(Film film, User user) {
        filmsLikes.get(film.getId()).remove(user.getId());
    }

    public List<Film> findAllByPopular(Integer count) {
        return findAll()
                .stream()
                .sorted((f1, f2) -> {
                    Integer size1 = filmsLikes.get(f1.getId()).size();
                    Integer size2 = filmsLikes.get(f2.getId()).size();
                    return -1 * size1.compareTo(size2);
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
