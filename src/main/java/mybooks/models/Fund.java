package mybooks.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the funds of the library.
 *
 */
@Entity
@Table(name = "funds")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Fund {
	
	@Id
	@Size(min = 4, max = 4, message = "Должно быть = 4 символа!")
	private String id;
	
	@NotBlank(message = "Имя библиотечного фонда обязательно!")
	@Size(min = 5, message = "Должно быть не < 5 символов!")
	private String name;
	
	private LocalDateTime createDate = LocalDateTime.now();
	
}
