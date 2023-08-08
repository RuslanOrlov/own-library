package mybooks.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the books of the library.
 *
 */
@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Название книги обязательно!")
	@Size(min = 1, message = "Должно быть не < 1 символа!")
	private String title;
	
	@NotBlank(message = "Описание книги обязательно!")
	@Size(min = 5, message = "Должно быть не < 5 символов!")
	private String description;
	
	// У одной книги может быть много авторов, 
	// а у одного автора может быть много книг
	@ManyToMany
	private List<Author> authors = new ArrayList<>();
	
	// Книга относится к одному жанру, а у 
	// одного жанра может быть много книг
	@ManyToOne
	private Genre genre;
	
	// Книга относится к одному фонду, а у 
	// одного фонда может быть много книг
	@ManyToOne
	private Fund fund;
	
	@Min(value = 1900, message = "Год печати не может быть меньше 1900-го!")
	private int printYear;
	
	private LocalDateTime createDate = LocalDateTime.now();
	
}
