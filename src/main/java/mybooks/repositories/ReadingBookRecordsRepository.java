package mybooks.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import mybooks.models.Book;
import mybooks.models.ReadingBookRecord;
import mybooks.models.User;

/**
 * @author Ruslan G. Orlov
 * This interface implements repository for the books that users read.
 * 
 */
public interface ReadingBookRecordsRepository 
			extends CrudRepository<ReadingBookRecord, Long>{
	
	List<ReadingBookRecord> getAllByIdNotNullAndUserEqualsOrderById(User user, Pageable pageable); 
	List<ReadingBookRecord> getAllByUserOrderById(User user); 
	List<ReadingBookRecord> getAllByBookOrderById(Book book); 
	List<ReadingBookRecord> getAllByUserAndBookAndIsReadingEndedIsFalse(User user, Book book); 
	List<ReadingBookRecord> getAllByUserAndBookAndIsReadingEndedIsTrue(User user, Book book); 
	long countByUser(User user); 
	
}
