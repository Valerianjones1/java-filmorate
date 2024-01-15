package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public UserStorage getStorage() {
        return storage;
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }

        return user;
    }

    public User removeFriend(Integer userId, Integer friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }

        return user;
    }

    public List<User> getFriends(Integer userId) {
        User user = storage.get(userId);

        List<User> friends;
        if (user != null) {
            Set<Integer> friendsIds = user.getFriends();
            friends = friendsIds.stream()
                    .map(storage::get)
                    .collect(Collectors.toList());
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", userId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = storage.get(userId);
        User otherUser = storage.get(otherUserId);

        List<User> commonFriends;
        if (user != null && otherUser != null) {
            Set<Integer> friendsIds = user.getFriends();
            Set<Integer> otherFriendsIds = otherUser.getFriends();
            Set<Integer> commonFriendsIds = friendsIds.stream()
                    .filter(otherFriendsIds::contains)
                    .collect(Collectors.toSet());
            commonFriends = commonFriendsIds.stream()
                    .map(storage::get)
                    .collect(Collectors.toList());
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return commonFriends;
    }
}
