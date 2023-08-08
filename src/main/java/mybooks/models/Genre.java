package mybooks.models;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the books genres.
 *
 */
@Entity
@Table(name = "genres")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Genre {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long 	id;
	
	@NotBlank(message = "Название жанра (рус.) обязательно!")
	@Size(min = 2, message = "Должно быть не < 2 символов!")
	private String 	nameRus;
	
	@NotBlank(message = "Название жанра (анг.) обязательно!")
	@Size(min = 2, message = "Должно быть не < 2 символов!")
	private String 	nameEng;
	
	private LocalDateTime createDate = LocalDateTime.now();
	
}
