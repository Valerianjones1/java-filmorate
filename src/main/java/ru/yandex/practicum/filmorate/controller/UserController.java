package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @GetMapping
    public List<User> findAllUsers() {
        return service.getStorage().findAll();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User addedUser = service.getStorage().add(user);
        log.debug("Добавил пользователя {}", user);
        return addedUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                log.warn(error.getDefaultMessage());
            }
            throw new ValidationException("Произошла ошибка валидации");
        }
        User updatedUser = service.getStorage().update(user);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Integer userId) {
        service.getStorage().remove(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return service.getStorage().get(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return service.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return service.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Integer userId) {
        return service.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Integer userId, @PathVariable Integer otherUserId) {
        return service.getCommonFriends(userId, otherUserId);
    }

}
