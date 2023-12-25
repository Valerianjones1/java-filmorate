package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    private Integer getIdCounter() {
        return idCounter++;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
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
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }

        if (film.getId() != null) {
            if (!films.containsKey(film.getId())) {
                log.warn("Такого фильма нет в библиотеке");
                throw new NotFoundException("Такого фильма нет в библиотеке.");
            }
            films.put(film.getId(), film);
            log.debug("Обновили фильм {}", film);
        } else {
            log.warn("Идентификатор фильма равен null");
            throw new ValidationException("Идентификатор фильма равен null");
        }

        return film;
    }

}
