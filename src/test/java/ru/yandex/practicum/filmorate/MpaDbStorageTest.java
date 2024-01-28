package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void findAllMpas() {
        MpaStorage mpaStorage = new MpaDbStorage(jdbcTemplate);

        List<Mpa> allMpas = mpaStorage.findAll();
        assertThat(allMpas)
                .isNotEmpty()
                .size()
                .isEqualTo(5);
    }

    @Test
    public void getMpa() {
        MpaStorage mpaStorage = new MpaDbStorage(jdbcTemplate);

        Mpa mpa = mpaStorage.get(1);
        assertThat(mpa)
                .isNotNull();
        assertThat(mpa.getName())
                .isEqualTo("G");
    }
}
