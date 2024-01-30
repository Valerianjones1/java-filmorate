package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcMpaRepositoryTest {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void findAllMpas() {
        MpaRepository mpaRepository = new JdbcMpaRepository(jdbcTemplate);

        List<Mpa> allMpas = mpaRepository.findAll();
        assertThat(allMpas)
                .isNotEmpty()
                .size()
                .isEqualTo(5);
    }

    @Test
    public void getMpa() {
        MpaRepository mpaRepository = new JdbcMpaRepository(jdbcTemplate);

        Mpa mpa = mpaRepository.get(1);
        assertThat(mpa)
                .isNotNull();
        assertThat(mpa.getName())
                .isEqualTo("G");
    }
}
