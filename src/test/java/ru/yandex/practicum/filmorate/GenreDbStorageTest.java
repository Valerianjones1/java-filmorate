package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void findAllGenres() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        List<Genre> allGenres = genreStorage.findAll();
        assertThat(allGenres)
                .isNotEmpty()
                .size()
                .isEqualTo(6);
    }

    @Test
    public void getGenre() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        Genre genre = genreStorage.get(1);
        assertThat(genre)
                .isNotNull();
        assertThat(genre.getName())
                .isEqualTo("Комедия");
    }
}

