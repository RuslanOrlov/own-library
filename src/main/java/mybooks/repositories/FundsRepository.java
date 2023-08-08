package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.Fund;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the library funds.
 * 
 */
public interface FundsRepository extends CrudRepository<Fund, String>{
	
	List<Fund> getAllByIdNotNull(Pageable pageable);
	
}
