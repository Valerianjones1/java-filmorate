package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    public User add(User user);

    public User update(User user);

    public void remove(Integer userId);

    public List<User> findAll();

    public User get(Integer userId);

    public User addFriend(User user, User friend);

    public User removeFriend(User user, User friend);

    public List<User> getFriends(Integer userId);

    public List<User> getCommonFriends(Integer userId, Integer otherUserId);
}
