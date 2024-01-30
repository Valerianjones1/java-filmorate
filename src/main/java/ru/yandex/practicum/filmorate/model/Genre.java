package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Genre {

    private Integer id;

    private String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre() {

    }
}
