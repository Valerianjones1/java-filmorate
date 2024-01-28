package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.add(user);
    }

    public User update(@Valid User user) {
        if (user.getId() == null) {
            log.warn("Идентификатор пользователя равен null");
            throw new ValidationException("Идентификатор пользователя равен null");
        }
        User updatedUser;

        if (storage.get(user.getId()) == null) {
            log.warn("Пользователь для обновления не найден");
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", user.getId()));
        }
        updatedUser = storage.update(user);
        log.debug("Обновил пользователя {}", user);
        return updatedUser;
    }

    public void remove(Integer userId) {
        storage.remove(userId);
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User get(Integer userId) {
        User foundUser = storage.get(userId);
        if (foundUser == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        return foundUser;
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        } else if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", friendId));
        }

        return storage.addFriend(user, friend);
    }

    public User removeFriend(Integer userId, Integer friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        } else if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", friendId));
        }

        return storage.removeFriend(user, friend);
    }

    public List<User> getFriends(Integer userId) {
        User user = storage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        return storage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = storage.get(userId);
        User otherUser = storage.get(otherUserId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        } else if (otherUser == null) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %s не найден", otherUserId));
        }

        return storage.getCommonFriends(userId, otherUserId);
    }

}
