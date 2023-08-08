package mybooks.configs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import mybooks.models.AppTheme;
import mybooks.models.Author;
import mybooks.models.Book;
import mybooks.models.Fund;
import mybooks.models.Genre;
import mybooks.models.User;
import mybooks.models.UserAuthorityTypes;
import mybooks.models.UserSettings;
import mybooks.models.UserAuthorityTypes.AuthorityType;
import mybooks.repositories.AppThemesRepository;
import mybooks.repositories.AuthorsRepository;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.FundsRepository;
import mybooks.repositories.GenresRepository;
import mybooks.repositories.UserAuthorityTypesRepository;
import mybooks.repositories.UsersRepository;
import mybooks.repositories.UsersSettingsRepository;

/**
 * @author Ruslan G. Orlov
 * This configuration class configures the entry of 
 * the initial data set into the application database. 
 * 
 */
@Configuration
public class DevConfig {
	
	@Bean
	public CommandLineRunner dataLoader(AuthorsRepository authorsRepository,
										FundsRepository fundsRepository,
										GenresRepository genresRepository,
										BooksRepository booksRepository, 
										UsersRepository usersRepository, 
										UserAuthorityTypesRepository userAuthoritiesRepositry, 
										UsersSettingsRepository settingsRepository, 
										AppThemesRepository themesRepository, 
										PasswordEncoder passwordEncoder) {
		return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				// Первичная инициализация списка авторов: 
				authorsRepository.save(new Author(1L, "ТЕСТОВЫЙ АВТОР_1", LocalDateTime.now()));
				authorsRepository.save(new Author(2L, "ТЕСТОВЫЙ АВТОР_2", LocalDateTime.now()));
				authorsRepository.save(new Author(3L, "ТЕСТОВЫЙ АВТОР_3", LocalDateTime.now()));
				authorsRepository.save(new Author(4L, "ТЕСТОВЫЙ АВТОР_4", LocalDateTime.now()));
				
				// Первичная инициализация списка фондов библиотеки: 
				fundsRepository.save(new Fund("TST1", "ТЕСТОВЫЙ ФОНД_1", LocalDateTime.now()));
				fundsRepository.save(new Fund("TST2", "ТЕСТОВЫЙ ФОНД_2", LocalDateTime.now()));
				fundsRepository.save(new Fund("SCN1", "НАУЧНЫЙ ФОНД_1", LocalDateTime.now()));
				fundsRepository.save(new Fund("SCN2", "НАУЧНЫЙ ФОНД_2", LocalDateTime.now()));
				fundsRepository.save(new Fund("CMN1", "ОБЩИЙ ФОНД_1", LocalDateTime.now()));
				fundsRepository.save(new Fund("CMN2", "ОБЩИЙ ФОНД_2", LocalDateTime.now()));
				
				// Первичная инициализация списка литертурных жанров: 
				// (источник сайт - https://en.wikipedia.org/wiki/Types_of_writing)
				genresRepository.save(new Genre(1L, "Экшен".toUpperCase(), 
													"Action".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(2L, "Приключения".toUpperCase(), 
													"Adventure".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(3L, "Юмор".toUpperCase(), 
													"Comedy".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(4L, "Криминал и детектив".toUpperCase(), 
													"Crime and mystery".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(5L, "Фэнтези".toUpperCase(), 
													"Fantasy".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(6L, "Исторический".toUpperCase(), 
													"Historical".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(7L, "Ужасы".toUpperCase(), 
													"Horror".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(8L, "Любовные романы".toUpperCase(), 
													"Romance".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(9L, "Сатира".toUpperCase(), 
													"Satire".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(10L, "Научная фантастика".toUpperCase(), 
													"Science fiction".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(11L, "Киберпанк".toUpperCase(), 
													"Cyberpunk".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(12L, "Триллер".toUpperCase(), 
													"Thriller".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(13L, "Вестерн".toUpperCase(), 
													"Western".toUpperCase(), 
													LocalDateTime.now()));
				genresRepository.save(new Genre(14L, "Научно-познавательная".toUpperCase(), 
													"Scientific and educational".toUpperCase(), 
													LocalDateTime.now()));
				
				// Первичная инициализация списка книг библиотеки: 
				Book book = new Book(1L, "Test book_1 - title", "Test book_1 - description", 
						Arrays.asList(authorsRepository.findById(1L).orElse(null), 
									  authorsRepository.findById(2L).orElse(null)), 
						genresRepository.findById(1L).orElse(null), 
						fundsRepository.findById("TST1").orElse(null), 
						2020, LocalDateTime.now());
				booksRepository.save(book);
				book = new Book(2L, "Test book_2 - title", "Test book_2 - description", 
						Arrays.asList(authorsRepository.findById(2L).orElse(null), 
									  authorsRepository.findById(3L).orElse(null)), 
						genresRepository.findById(14L).orElse(null), 
						fundsRepository.findById("SCN1").orElse(null), 
						2021, LocalDateTime.now());
				booksRepository.save(book);
				
				// Первичная инициализация списка привилегий (ролей) пользователей
				userAuthoritiesRepositry.save(new UserAuthorityTypes(
						1L, AuthorityType.ROLE_ADMIN, "роль администратора"));
				userAuthoritiesRepositry.save(new UserAuthorityTypes(
						2L, AuthorityType.ROLE_VIEW, "роль для просмотра всех данных"));
				userAuthoritiesRepositry.save(new UserAuthorityTypes(
						3L, AuthorityType.ROLE_ADD_EDIT, "роль для добавления/редактирования данных")); 
				userAuthoritiesRepositry.save(new UserAuthorityTypes(
						4L, AuthorityType.ROLE_DELETE, "роль для удаления данных"));
				
				// Первичная инициализация тем приложения
				themesRepository.save(new AppTheme(1L, 
										"/styles-standard.css".toUpperCase(), 
										"Стандартная".toUpperCase()));
				themesRepository.save(new AppTheme(2L, 
										"/styles-dark.css".toUpperCase(), 
										"Тёмная".toUpperCase()));
				
				// Первичная инициализация пользователей с соответствующими привилегями (ролями)
				UserSettings userSettings = new UserSettings(
						themesRepository.findById(1L).orElse(null));
				usersRepository.save(
						new User("admin", passwordEncoder.encode(""), "admin", 
								LocalDate.now(), "5555555",  
								Arrays.asList(
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_ADMIN)), 
								userSettings)); 
				
				userSettings = new UserSettings(
						themesRepository.findById(1L).orElse(null));
				usersRepository.save(
						new User("full", passwordEncoder.encode(""), "full", 
								LocalDate.now(), "5555555", 
								Arrays.asList(
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_VIEW), 
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_ADD_EDIT), 
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_DELETE)), 
								userSettings)); 
				
				userSettings = new UserSettings(
						themesRepository.findById(1L).orElse(null));
				usersRepository.save(
						new User("user1", passwordEncoder.encode("1"), "user1", 
								LocalDate.now(), "5555555", 
								Arrays.asList(
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_VIEW)), 
								userSettings)); 
				
				userSettings = new UserSettings(
						themesRepository.findById(1L).orElse(null));
				usersRepository.save(
						new User("user2", passwordEncoder.encode("1"), "user2", 
								LocalDate.now(), "5555555", 
								Arrays.asList(
										userAuthoritiesRepositry.
											findByAuthority(AuthorityType.ROLE_VIEW)), 
								userSettings)); 
			}
			
		};
	}
	
}
