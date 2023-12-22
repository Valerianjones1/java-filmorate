package ru.yandex.practicum.filmorate;

import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;
    private Film film;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        film = new Film();
        film.setId(0);
        film.setName("test film");
        film.setDuration(125);
        film.setReleaseDate(LocalDate.now());
        film.setDescription("test");
    }

    @Test
    public void shouldCreateFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCreateFilmNegativeId() {
        film.setId(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        String message = violations.stream().collect(Collectors.toList()).get(0).getMessage();


        assertFalse(violations.isEmpty());
        assertEquals("Идентификатор не может быть меньше нуля", message);
    }

    @Test
    public void shouldCreateFilmBorderReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldCreateFilmBorderDescription() {
        film.setDescription("f".repeat(200));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCreateFilmOutBorderDescription() {
        film.setDescription("f".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String message = violations.stream().collect(Collectors.toList()).get(0).getMessage();

        assertFalse(violations.isEmpty());
        assertEquals("Длина описания превышает лимит в 200 символов", message);
    }

    @Test
    public void shouldCreateFilmDuration() {
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCreateFilmDuration() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        String message = violations.stream().collect(Collectors.toList()).get(0).getMessage();

        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность не может быть меньше или равна нулю", message);
    }
}
