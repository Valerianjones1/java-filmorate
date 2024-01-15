package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    private Integer getIdCounter() {
        return idCounter++;
    }

    @Override
    public User add(User user) {
        user.setFriends(new HashSet<>());
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() != null) {
            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь для обновления не найден");
                throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", user.getId()));
            }
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }
            users.put(user.getId(), user);
            log.debug("Обновил пользователя {}", user);
        } else {
            log.warn("Идентификатор пользователя равен null");
            throw new ValidationException("Идентификатор пользователя равен null");
        }
        return user;
    }

    @Override
    public void remove(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        users.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        return users.get(userId);
    }
}
