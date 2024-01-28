package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("ID");
        Integer userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();

        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return user;
    }

    @Override
    public void remove(Integer userId) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";

        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User get(Integer userId) {

        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public User addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO user_relation (user_id, other_user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
        return user;
    }

    @Override
    public User removeFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM user_relation WHERE (user_id = ? AND other_user_id = ?) OR (user_id = ? AND other_user_id = ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId(), friend.getId(), user.getId());
        return user;
    }

    @Override
    public List<User> getFriends(Integer userId) {
        String sqlQuery = "SELECT * FROM users as u " +
                "WHERE u.id IN (SELECT other_user_id FROM user_relation as ur WHERE ur.user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sqlQuery = "SELECT * " +
                "FROM users as u " +
                "WHERE u.id in " +
                "(SELECT other_user_id FROM user_relation as ur " +
                "WHERE ur.user_id=? and other_user_id in " +
                "(SELECT other_user_id FROM user_relation as ur1 WHERE ur1.user_id = ?))";

        return jdbcTemplate.query(sqlQuery, this::makeUser, userId, otherUserId);
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User initialUser = new User(resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());

        List<Map<String, Object>> friends = jdbcTemplate.queryForList(
                "SELECT * FROM user_relation WHERE user_id = ?",
                initialUser.getId());

        if (!friends.isEmpty()) {
            Map<Integer, String> friendsHash = new HashMap<>();
            for (Map<String, Object> row : friends) {
                Integer other_user_id = (Integer) row.get("other_user_id");
                String status = (String) row.get("status");
                friendsHash.put(other_user_id, status);
            }
            initialUser.setFriends(friendsHash);
        }

        return initialUser;
    }
}
