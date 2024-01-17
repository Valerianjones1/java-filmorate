package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
        return service.findAll();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        User addedUser = service.add(user);
        log.debug("Добавил пользователя {}", addedUser);
        return addedUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User updatedUser = service.update(user);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Integer userId) {
        service.remove(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return service.get(userId);
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
