package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    @NotNull(message = "Название не может быть null")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    private String description;

//    @DateTimeFormat
    private LocalDate releaseDate;
    @Positive
    private int duration; // Продолжительность фильма в минутах
}
