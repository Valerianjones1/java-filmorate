package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.get(newUser.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdate() {
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        User foundOldUser = userStorage.get(newUser.getId());

        User updatedUser = new User(newUser.getId(), "user@email.ru", "vanya123", "Update Ivan Petrov", LocalDate.of(1994, 1, 1));

        userStorage.update(updatedUser);
        // вызываем тестируемый метод
        User foundUpdUser = userStorage.get(updatedUser.getId());

        // проверяем утверждения
        assertThat(foundUpdUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isNotEqualTo(foundOldUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testRemove() {
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        User addedUser = userStorage.get(newUser.getId());

        userStorage.remove(addedUser.getId());

        User removedUser = userStorage.get(newUser.getId());

        // вызываем тестируемый метод

        // проверяем утверждения
        assertThat(removedUser)
                .isNotEqualTo(addedUser)
                .isNull();
    }

    @Test
    public void testFindAll() {

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        List<User> emptyUsers = userStorage.findAll();
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser);
        List<User> filledUsers = userStorage.findAll();


        // вызываем тестируемый метод

        // проверяем утверждения
        assertThat(filledUsers)
                .isNotEqualTo(emptyUsers);

        assertThat(emptyUsers.size())
                .isNotEqualTo(filledUsers.size())
                .isEqualTo(0);
    }

    @Test
    public void testAddFriend() {

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        // Подготавливаем данные для теста
        User newUser1 = new User("user1@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser1);

        User newUser2 = new User("user2@email.ru", "fvanyza123", "Valera Petrov", LocalDate.of(1991, 1, 1));
        userStorage.add(newUser2);

        User friendUser = userStorage.addFriend(newUser1, newUser2);

        List<User> friends = userStorage.getFriends(friendUser.getId());

        assertThat(friends)
                .isNotNull()
                .size()
                .isEqualTo(1);

        assertThat(friends.get(0))
                .isEqualTo(newUser2);
    }

    @Test
    public void testRemoveFriend() {

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        // Подготавливаем данные для теста
        User newUser1 = new User("user1@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser1);

        User newUser2 = new User("user2@email.ru", "fvanyza123", "Valera Petrov", LocalDate.of(1991, 1, 1));
        userStorage.add(newUser2);

        User friendUser = userStorage.addFriend(newUser1, newUser2);

        List<User> friends = userStorage.getFriends(friendUser.getId());

        userStorage.removeFriend(newUser1, newUser2);

        List<User> removedFriends = userStorage.getFriends(friendUser.getId());

        assertThat(removedFriends.size())
                .isEqualTo(0);

        assertThat(friends)
                .isNotEqualTo(removedFriends)
                .isNotNull()
                .size()
                .isEqualTo(1);

        assertThat(friends.get(0))
                .isEqualTo(newUser2);
    }

    @Test
    public void testGetCommonFriends() {

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        // Подготавливаем данные для теста
        User newUser1 = new User("user1@email.ru", "vanyza123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser1);

        User newUser2 = new User("user2@email.ru", "fvanyza123", "Valera Petrov", LocalDate.of(1991, 1, 1));
        userStorage.add(newUser2);

        User newUser3 = new User("user3@email.ru", "fcvanyza123", "Valerxa Petrov", LocalDate.of(1991, 1, 1));
        userStorage.add(newUser3);

        User friendUser1 = userStorage.addFriend(newUser1, newUser3);

        User friendUser2 = userStorage.addFriend(newUser2, newUser3);

        List<User> commonFriends = userStorage.getCommonFriends(friendUser1.getId(), friendUser2.getId());

        assertThat(commonFriends)
                .isNotNull()
                .contains(newUser3);
        assertThat(commonFriends.size())
                .isEqualTo(1);
    }


}