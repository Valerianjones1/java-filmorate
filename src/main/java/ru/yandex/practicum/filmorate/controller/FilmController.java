package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParamException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return service.getStorage().findAll();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }
        Film addedFilm = service.getStorage().add(film);
        log.debug("Добавили фильм {}", addedFilm);
        return addedFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }

        Film updatedFilm = service.getStorage().update(film);
        return updatedFilm;
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Integer filmId) {
        service.getStorage().remove(filmId);
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Integer filmId) {
        return service.getStorage().get(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film likeFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return service.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count < 0) {
            throw new IncorrectParamException("Параметр count не может быть меньше нуля.");
        }
        return service.getPopularFilms(count);
    }

}
