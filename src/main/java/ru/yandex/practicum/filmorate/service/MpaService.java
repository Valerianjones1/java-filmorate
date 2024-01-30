package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    @Autowired
    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public List<Mpa> findAll() {
        return mpaRepository.findAll();
    }

    public Mpa getMpaById(Integer mpaId) {
        Mpa mpa = mpaRepository.get(mpaId);
        if (mpa == null) {
            throw new NotFoundException(String.format("Рейтинг %s не был найден", mpaId));
        }
        return mpa;
    }
}
