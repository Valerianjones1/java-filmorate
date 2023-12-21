package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    public Integer getIdCounter() {
        return idCounter++;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Произошла ошибка валидации");
            throw new ValidationException("Произошла ошибка валидации");
        }
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        log.debug("Добавили фильм {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Произошла ошибка валидации");
            throw new ValidationException("Произошла ошибка валидации");
        }
        if (!films.keySet().contains(film.getId())) {
            log.warn("Такого фильма нет в библиотеке");
            throw new ValidationException("Такого фильма нет в библиотеке.");
        }
        films.put(film.getId(), film);
        log.debug("Обновили фильм {}", film);

        return film;
    }

}
