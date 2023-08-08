package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.Author;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the books authors.
 * 
 */
public interface AuthorsRepository extends CrudRepository<Author, Long>{
	
	List<Author> getAllByIdGreaterThanEqual(long start, Pageable pageable);
	
}
