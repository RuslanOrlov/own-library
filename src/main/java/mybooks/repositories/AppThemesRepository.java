package mybooks.repositories;

import org.springframework.data.repository.CrudRepository;

import mybooks.models.AppTheme;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the application themes.
 * 
 */
public interface AppThemesRepository extends CrudRepository<AppTheme, Long>{

}
