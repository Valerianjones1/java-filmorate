package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRepository {
    public List<Mpa> findAll();

    public Mpa get(Integer mpaId);
}
