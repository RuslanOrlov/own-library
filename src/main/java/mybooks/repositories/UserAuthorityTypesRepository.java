package mybooks.repositories;

import org.springframework.data.repository.CrudRepository;

import mybooks.models.UserAuthorityTypes;
import mybooks.models.UserAuthorityTypes.AuthorityType;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the user authority types.
 * 
 */
public interface UserAuthorityTypesRepository extends CrudRepository<UserAuthorityTypes, Long>{
	
	UserAuthorityTypes findByAuthority(AuthorityType authority);
	
}
