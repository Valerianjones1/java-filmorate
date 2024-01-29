package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class Film {
    private Integer id;

    @NotNull(message = "Название не может быть null")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть null")
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Длина описания превышает лимит в 200 символов")
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность не может быть меньше или равна нулю")
    private int duration; // Продолжительность фильма в минутах

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private Integer rate;


    private Mpa mpa;

    public Film(Integer id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, int duration, Integer rate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film() {
    }
}
