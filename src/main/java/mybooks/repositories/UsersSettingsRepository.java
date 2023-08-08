package mybooks.repositories;

import org.springframework.data.repository.CrudRepository;

import mybooks.models.UserSettings;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the application users settings.
 * 
 */
public interface UsersSettingsRepository extends CrudRepository<UserSettings, Long>{

}
