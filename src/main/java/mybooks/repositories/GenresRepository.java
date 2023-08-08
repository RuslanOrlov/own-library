package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.Genre;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the books genres.
 * 
 */
public interface GenresRepository extends CrudRepository<Genre, Long>{
	
	List<Genre> getAllByIdGreaterThanEqual(long start, Pageable pageable);
	
}
