package mybooks.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Ruslan G. Orlov
 * This class represents the model of the library books that users read.
 *
 */
@Entity
@Table(name = "reading_records")
@Data
public class ReadingBookRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// Одна запись о прочтении книги всегда относится к одному 
	// пользователю, а у одного пользователя может быть много 
	// записей о прочтенных книгах (пользователь может брать как 
	// много разных книг, так и много раз одну и ту же книгу). 
	@ManyToOne
	private User user;
	
	private LocalDateTime startDate = LocalDateTime.now(); 
	private LocalDateTime endDate; 
	private boolean isReadingEnded = false; 
	
	// Одна запись о прочтении книги всегда относится к одной книге, 
	// а у одной книги может быть много записей о её прочтении. 
	@ManyToOne
	private Book book;

}
