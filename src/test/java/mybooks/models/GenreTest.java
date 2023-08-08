package mybooks.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class GenreTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testId() {
        Genre genre = new Genre();
        genre.setId(1L);
        assertEquals(1L, genre.getId());
    }

    @Test
    public void testNameRus() {
        Genre genre = new Genre();
        genre.setNameRus("Научная фантастика");
        assertEquals("Научная фантастика", genre.getNameRus());

        // Test @NotBlank constraint
        genre.setNameRus("");
        Set<ConstraintViolation<Genre>> violations = validator.validate(genre);
        assertFalse(violations.isEmpty());
        assertEquals("Название жанра (рус.) обязательно!", violations.iterator().next().getMessage());

        // Test @Size constraint
        genre.setNameRus("к");
        violations = validator.validate(genre);
        assertFalse(violations.isEmpty());
        assertEquals("Должно быть не < 2 символов!", violations.iterator().next().getMessage());
    }

    @Test
    public void testNameEng() {
        Genre genre = new Genre();
        genre.setNameEng("Science Fiction");
        assertEquals("Science Fiction", genre.getNameEng());

        // Test @NotBlank constraint
        genre.setNameEng("");
        Set<ConstraintViolation<Genre>> violations = validator.validate(genre);
        assertFalse(violations.isEmpty());
        assertEquals("Название жанра (анг.) обязательно!", violations.iterator().next().getMessage());

        // Test @Size constraint
        genre.setNameEng("s");
        violations = validator.validate(genre);
        assertFalse(violations.isEmpty());
        assertEquals("Должно быть не < 2 символов!", violations.iterator().next().getMessage());
    }

    @Test
    public void testCreateDate() {
        LocalDateTime now = LocalDateTime.now();

        Genre genre = new Genre();
        assertNotNull(genre.getCreateDate());
        assertTrue(now.isBefore(genre.getCreateDate().plusSeconds(1)));
    }
}
