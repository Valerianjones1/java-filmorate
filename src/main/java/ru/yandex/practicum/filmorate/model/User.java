package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @PositiveOrZero(message = "Идентификатор не может быть меньше нуля")
    private int id;

    @NotNull(message = "Почта не может быть равна null")
    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта не соответствует требованиям")
    private String email;

    @NotNull(message = "Логин не может быть равен null")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message ="Пробелы запрещены в логине")
    private String login;

    private String name;

    @PastOrPresent(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
}
