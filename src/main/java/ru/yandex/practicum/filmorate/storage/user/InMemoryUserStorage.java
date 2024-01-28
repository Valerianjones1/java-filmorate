package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> usersFriends = new HashMap<>();
    private int idCounter = 1;

    private Integer getIdCounter() {
        return idCounter++;
    }

    @Override
    public User add(User user) {
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        usersFriends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void remove(Integer userId) {
        users.remove(userId);
        usersFriends.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(Integer userId) {
        return users.get(userId);
    }

    public User addFriend(User user, User friend) {
        usersFriends.get(user.getId()).add(friend.getId());
        usersFriends.get(friend.getId()).add(user.getId());
        return user;
    }

    public User removeFriend(User user, User friend) {
        usersFriends.get(user.getId()).remove(friend.getId());
        usersFriends.get(friend.getId()).remove(user.getId());
        return user;
    }

    @Override
    public List<User> getFriends(Integer userId) {
        Set<Integer> friendsIds = usersFriends.get(userId);
        return friendsIds.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        Set<Integer> friendsIds = usersFriends.get(userId);
        Set<Integer> otherFriendsIds = usersFriends.get(otherUserId);
        Set<Integer> commonFriendsIds = friendsIds.stream()
                .filter(otherFriendsIds::contains)
                .collect(Collectors.toSet());

        return commonFriendsIds.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }
}
