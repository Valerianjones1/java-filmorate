package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("UserDbStorage")
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (:email, :login, :name, :birthday)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource userParams = new MapSqlParameterSource(userToMap(user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday()));

        jdbcTemplate.update(sqlQuery, userParams, keyHolder);

        Integer userId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET " +
                "email = :email, login = :login, name = :name, birthday = :birthday " +
                "WHERE id = :id";

        SqlParameterSource userParams = new MapSqlParameterSource(Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday(),
                "id", user.getId()));

        jdbcTemplate.update(sqlQuery, userParams);
        return user;
    }

    @Override
    public void remove(Integer userId) {
        String sqlQuery = "DELETE FROM users WHERE id = :id";

        SqlParameterSource params = new MapSqlParameterSource("id", userId);

        jdbcTemplate.update(sqlQuery, params);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User get(Integer userId) {

        String sqlQuery = "SELECT * FROM users WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource("id", userId);

        List<User> users = jdbcTemplate.query(sqlQuery, params, this::makeUser);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User addFriend(User user, User friend) {
        String sqlQuery = "MERGE INTO user_relation as ur " +
                "USING (VALUES(:user_id, :other_user_id)) as v(user_id,other_user_id) " +
                "ON ur.user_id=v.user_id and ur.other_user_id=v.other_user_id " +
                "WHEN MATCHED THEN UPDATE SET ur.user_id=v.user_id and ur.other_user_id=v.other_user_id " +
                "WHEN NOT MATCHED THEN INSERT (user_id,other_user_id) VALUES(:user_id, :other_user_id)";

        SqlParameterSource params = new MapSqlParameterSource(Map.of(
                "user_id", user.getId(),
                "other_user_id", friend.getId()));

        jdbcTemplate.update(sqlQuery, params);
        return user;
    }

    @Override
    public User removeFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM user_relation WHERE (user_id = :user_id AND other_user_id = :other_user_id) " +
                "OR (user_id = :other_user_id AND other_user_id = :user_id)";
        SqlParameterSource params = new MapSqlParameterSource(Map.of(
                "user_id", user.getId(),
                "other_user_id", friend.getId()));
        jdbcTemplate.update(sqlQuery, params);
        return user;
    }

    @Override
    public List<User> getFriends(Integer userId) {
        String sqlQuery = "SELECT * FROM users as u " +
                "WHERE u.id IN (SELECT other_user_id FROM user_relation as ur WHERE ur.user_id = :user_id)";
        SqlParameterSource params = new MapSqlParameterSource("user_id", userId);
        return jdbcTemplate.query(sqlQuery, params, this::makeUser);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sqlQuery = "SELECT * " +
                "FROM users as u " +
                "WHERE u.id in " +
                "(SELECT other_user_id FROM user_relation as ur " +
                "WHERE ur.user_id=:user_id and other_user_id in " +
                "(SELECT other_user_id FROM user_relation as ur1 WHERE ur1.user_id = :other_user_id))";

        SqlParameterSource params = new MapSqlParameterSource(Map.of(
                "user_id", userId,
                "other_user_id", otherUserId));

        return jdbcTemplate.query(sqlQuery, params, this::makeUser);
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
    }

    public Map<String, Object> userToMap(String email, String login,
                                         String name, LocalDate birthday) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
