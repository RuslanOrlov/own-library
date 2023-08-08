package mybooks.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import mybooks.repositories.AuthorsRepository;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.FundsRepository;
import mybooks.repositories.GenresRepository;

@DataJpaTest
public class BookTest {

    @Autowired
    private FundsRepository fundRepository;

    @Autowired
    private GenresRepository genreRepository;

    @Autowired
    private AuthorsRepository authorRepository;

    @Autowired
    private BooksRepository bookRepository;

    @Test
    public void testCreateBook() {
        Fund fund = new Fund("TEST", "Test Fund", LocalDateTime.now());
        fundRepository.save(fund);

        Genre genre = new Genre(null, "Роман", "Novel", null);
        genreRepository.save(genre);

        Author author = new Author(null, "Test Author", null);
        authorRepository.save(author);

        Book book = new Book(null, "Test Book", "Test Description", 
        						List.of(author), genre, fund, 2023, null);
        bookRepository.save(book);

        assertNotNull(book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Description", book.getDescription());
        assertEquals(1, book.getAuthors().size());
        assertEquals("Test Author", book.getAuthors().get(0).getFullname());
        assertEquals("Роман", book.getGenre().getNameRus());
        assertEquals("Test Fund", book.getFund().getName());
        assertEquals(2023, book.getPrintYear());
        assertNotNull(book.getCreateDate());
    }

}
