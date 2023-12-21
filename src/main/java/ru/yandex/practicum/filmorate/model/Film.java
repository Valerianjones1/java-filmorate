package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    @PositiveOrZero(message = "Идентификатор не может быть меньше нуля")
    private int id;

    @NotNull(message = "Название не может быть null")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть null")
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Длина описания превышает лимит в 200 символов")
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность не может быть меньше нуля")
    private int duration; // Продолжительность фильма в минутах
}
