package mybooks.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import mybooks.repositories.AuthorsRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AuthorTest {

    @Autowired
    private AuthorsRepository authorRepository;

    @Test
    public void testSaveAuthor() {
        Author author = new Author(null, "John Doe", LocalDateTime.now());
        assertNull(author.getId());
        author = authorRepository.save(author);
        assertNotNull(author.getId());
    }

    @Test
    public void testGetAuthor() {
        Author author = new Author(null, "John Doe", LocalDateTime.now());
        author = authorRepository.save(author);

        Author retrievedAuthor = authorRepository.findById(author.getId()).orElse(null);
        assertNotNull(retrievedAuthor);
        assertEquals("John Doe", retrievedAuthor.getFullname());
    }

    @Test
    public void testUpdateAuthor() {
        Author author = new Author(null, "John Doe", LocalDateTime.now());
        author = authorRepository.save(author);
        author.setFullname("Jane Doe");
        authorRepository.save(author);

        Author updatedAuthor = authorRepository.findById(author.getId()).orElse(null);
        assertNotNull(updatedAuthor);
        assertEquals("Jane Doe", updatedAuthor.getFullname());
    }

    @Test
    public void testDeleteAuthor() {
        Author author = new Author(null, "John Doe", LocalDateTime.now());
        author = authorRepository.save(author);
        authorRepository.delete(author);

        assertTrue(authorRepository.findById(author.getId()).isEmpty());
    }
}
