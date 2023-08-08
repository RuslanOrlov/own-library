package mybooks.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the user authority types. 
 * 
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserAuthorityTypes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	@Enumerated(value = EnumType.STRING)
	private final AuthorityType authority;
	private String description;
	
	public enum AuthorityType {
		ROLE_ADMIN, ROLE_VIEW, ROLE_ADD_EDIT, ROLE_DELETE
	}
	
}
