package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User add(User user);

    public User update(User user);

    public void remove(Integer filmId);

    public List<User> findAll();

    public User get(Integer userId);
}
