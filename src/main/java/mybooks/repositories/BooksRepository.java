package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.Author;
import mybooks.models.Book;
import mybooks.models.Fund;
import mybooks.models.Genre;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the library books.
 * 
 */
public interface BooksRepository extends CrudRepository<Book, Long>{
	
	List<Book> getAllByIdGreaterThanEqual(long start, Pageable pageable);
	List<Book> getAllByAuthorsContains(Author author);
	List<Book> getAllByFund(Fund fund);
	List<Book> getAllByGenre(Genre genre);
	
}
