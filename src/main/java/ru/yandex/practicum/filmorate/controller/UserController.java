package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    private Integer getIdCounter() {
        return idCounter++;
    }


    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        log.debug("Добавил пользователя {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }
        if (user.getId() != null) {
            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь для обновления не найден");
                throw new NotFoundException("Пользователь для обновления не найден");
            }
            users.put(user.getId(), user);
            log.debug("Обновил пользователя {}", user);
        } else {
            log.warn("Идентификатор пользователя равен null");
            throw new ValidationException("Идентификатор пользователя равен null");
        }
        return user;
    }

}
