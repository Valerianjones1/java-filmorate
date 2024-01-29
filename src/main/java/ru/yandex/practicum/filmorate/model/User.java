package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

@Data
public class User {
    private Integer id;

    @NotNull(message = "Почта не может быть равна null")
    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта не соответствует требованиям")
    private String email;

    @NotNull(message = "Логин не может быть равен null")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Пробелы запрещены в логине")
    private String login;

    private String name;

    @PastOrPresent(message = "День рождения не может быть в будущем")
    private LocalDate birthday;

    private Map<Integer, String> friends;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {

    }
}
