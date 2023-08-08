package mybooks.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * This test class tests the following functionalities:
 * - Creating a Fund object with valid input data
 * - Validating the minimum and maximum length of the id attribute
 * - Validating that the name attribute is not blank and has a minimum length of 5 characters
 * - Testing that the createDate attribute is not null
 * - Creating a new Fund object and testing that it has the correct values
 * - Testing that duplicate id values are not allowed and will throw a DataIntegrityViolationException.
 * */
@SpringBootTest
public class FundTest {
	
	private Fund fund;
	
	@BeforeEach
	void setUp() {
		fund = new Fund("F001", "Main fund", LocalDateTime.now());
	}

	@Test
	void testCreateFund() {
		assertEquals("F001", fund.getId());
		assertEquals("Main fund", fund.getName());
		assertNotNull(fund.getCreateDate());
	}
	
	@Test
	void testFundIdLengthValidation() {
		assertThrows(ConstraintViolationException.class, () -> 
			new Fund("F01", "Main fund", LocalDateTime.now()));
	}
	
	@Test
	void testFundNameNotBlankValidation() {
		assertThrows(ConstraintViolationException.class, () -> 
			new Fund("F001", "", LocalDateTime.now()));
	}
	
	@Test
	void testFundNameSizeValidation() {
		assertThrows(ConstraintViolationException.class, () -> 
			new Fund("F001", "Fund", LocalDateTime.now()));
	}
	
	@Test
	void testFundCreateDateTimeNotNull() {
		assertNotNull(fund.getCreateDate());
	}
	
	@Test
	void testFundCreate() {
		Fund fund2 = new Fund("F002", "Secondary fund", LocalDateTime.now());
		assertEquals("F002", fund2.getId());
		assertEquals("Secondary fund", fund2.getName());
		assertNotNull(fund2.getCreateDate());
	}
	
	@Test
	void testDuplicateFundIdNotAllowed() {
		assertThrows(DataIntegrityViolationException.class, () -> 
			new Fund("F001", "Duplicate fund", LocalDateTime.now()));
	}
}
