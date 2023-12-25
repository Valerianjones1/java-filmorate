package ru.yandex.practicum.filmorate;

import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private Validator validator;
    private User user;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User();
        user.setEmail("test@gmail.com");
        user.setBirthday(LocalDate.of(2000, 11, 16));
        user.setId(0);
        user.setLogin("test");
    }

    @Test
    public void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCreateUserSpacesLogin() {
        user.setLogin("test test");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        String message = violations.stream().collect(Collectors.toList()).get(0).getMessage();


        assertFalse(violations.isEmpty());
        assertEquals("Пробелы запрещены в логине", message);
    }

    @Test
    public void shouldCreateUserEmptyName() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldCreateUserBirthdayNow() {
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotCreateUserBirthdayNowPlusDay() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String message = violations.stream().collect(Collectors.toList()).get(0).getMessage();

        assertFalse(violations.isEmpty());
        assertEquals("День рождения не может быть в будущем", message);
    }
}
