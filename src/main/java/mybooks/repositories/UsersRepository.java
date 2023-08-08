package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.User;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the users of the application.
 * 
 */
public interface UsersRepository extends CrudRepository<User, Long>{
	
	User findByUsername(String username);
	List<User> getAllByIdGreaterThanEqual(long start, Pageable pageable);

}
