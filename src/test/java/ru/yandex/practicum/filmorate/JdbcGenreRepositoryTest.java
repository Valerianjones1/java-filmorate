package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcGenreRepositoryTest {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void findAllGenres() {
        GenreRepository genreRepository = new JdbcGenreRepository(jdbcTemplate);

        List<Genre> allGenres = genreRepository.findAll();
        assertThat(allGenres)
                .isNotEmpty()
                .size()
                .isEqualTo(6);
    }

    @Test
    public void getGenre() {
        GenreRepository genreRepository = new JdbcGenreRepository(jdbcTemplate);

        Genre genre = genreRepository.get(1);
        assertThat(genre)
                .isNotNull();
        assertThat(genre.getName())
                .isEqualTo("Комедия");
    }
}

