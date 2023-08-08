package mybooks.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.Author;
import mybooks.models.Book;
import mybooks.models.Fund;
import mybooks.models.Genre;
import mybooks.models.ReadingBookRecord;
import mybooks.repositories.AuthorsRepository;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.FundsRepository;
import mybooks.repositories.GenresRepository;
import mybooks.repositories.ReadingBookRecordsRepository;

/**
 * @author Ruslan G. Orlov
 * This Controller controls the changes of the 
 * books including their deletions and updates.
 */
@Slf4j
@Controller
@SessionAttributes("book")
@RequestMapping("/books/current")
public class ChangeBookController {

	private BooksRepository booksRepository;
	private ReadingBookRecordsRepository readingsRepository;
	private GenresRepository genresRepository;
	private FundsRepository fundsRepository;
	private AuthorsRepository authorsRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	public ChangeBookController(BooksRepository booksRepository, 
								ReadingBookRecordsRepository readingsRepository, 
								GenresRepository genresRepository,
								FundsRepository fundsRepository, 
								AuthorsRepository authorsRepository, 
								MyServicesClass service, 
								UriChain uriChain) {
		this.booksRepository = booksRepository;
		this.readingsRepository = readingsRepository;
		this.genresRepository = genresRepository;
		this.fundsRepository = fundsRepository;
		this.authorsRepository = authorsRepository;
		this.service = service;
		this.uriChain = uriChain;
	}
	
	@ModelAttribute(name = "theme")
	public String theme() {
		return this.service.themeApp();
	}
	
	@ModelAttribute(name = "username")
	public String getCurrentUserName() {
		return this.service.currentUserName();
	}
	
	@ModelAttribute(name = "isLoggedIn")
	public boolean isLoggedIn() {
		return this.service.isLoggedIn();
	}

	@ModelAttribute(name = "currentUserId")
	public long currentUserId() {
		return this.service.currentUserId();
	}
	
	@ModelAttribute(name = "curUriStat")
	public String curUriStat(HttpServletRequest httpServletRequest) {
		String value = null;
		
		if (this.isLoggedIn())
			value = httpServletRequest.getRequestURI() + this.service.getLeadNoteWithStat();
		else 
			value = this.service.getLeadNoteWithoutStat();
		
		return value + this.uriChain.currentStat(this.isLoggedIn());
	}
	
	@ModelAttribute(name = "book") 
	public Book book() { 
		return new Book(); 
	}
	
	@ModelAttribute(name = "genres")
	public List<Genre> genres() {
		return (List<Genre>) genresRepository.findAll();
	}
	
	@ModelAttribute(name = "funds")
	public List<Fund> funds() {
		return (List<Fund>) fundsRepository.findAll();
	}
	
	@ModelAttribute(name = "authors")
	public List<Author> authors() {
		return (List<Author>) authorsRepository.findAll();
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/new")
	public String openCreateForm(HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "new-book";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/create")
	public String createBook(@Valid Book book, Errors errors, 
							SessionStatus sessionStatus) {
		if (errors.hasErrors())
			return "new-book";
		
		booksRepository.save(book);
		sessionStatus.setComplete();
		
		log.info("Book was created: {}", book);
		
		return "redirect:/books";
	}

	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/{id}/edit")
	public String openEditForm(@PathVariable long id, Model model,
								HttpServletRequest httpServletRequest) {
		Book book = booksRepository.findById(id).orElse(null);
		if (book == null)
			return "redirect:/books";
		model.addAttribute("book", book);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-book";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/update")
	public String updateBook(@Valid Book book, Errors errors, 
							SessionStatus sessionStatus) {
		if (errors.hasErrors())
			return "edit-book";
		
		booksRepository.save(book);
		sessionStatus.setComplete();
		
		log.info("Book was updated: {}", book);
		
		return "redirect:/books";
	}

	@RequestMapping(value = "/change", 
					method = {RequestMethod.GET, RequestMethod.POST})
	public String returnCreateForm(@ModelAttribute Book book) {
		if (book.getId() == null)
			return "new-book"; 
		return "edit-book"; 
	}
	
	@GetMapping("/genres")
	public String choiceGenre(HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "choice-genre";
	}
	
	@GetMapping("/funds")
	public String choiceFund(HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "choice-fund";
	}
	
	@GetMapping("/authors")
	public String choiceAuthors(HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "choice-authors";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable long id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранную книгу? "
									+ "В случае удаления книги все ссылки на нее в записях "
									+ "прочитанных книг (в разделе 'Мои книги' пользователей) "
									+ "будут обнулены! Если удаление книги преждевременно, вы "
									+ "можете перейти к ее редактированию.");
		model.addAttribute("actionUrl_1", 	"/books/current");
		model.addAttribute("action_1", 		"/remove");
		model.addAttribute("button_1", 		"Удалить");
		model.addAttribute("actionUrl_2", 	"/books/current");
		model.addAttribute("action_2", 		"/edit");
		model.addAttribute("button_2", 		"Редактировать");
		model.addAttribute("listRootUrl", 	"/books");
		model.addAttribute("button_cancel", "Отменить");
		return "request-form";
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/{id}/remove")
	public String removeBook(@PathVariable long id) {
		Book book = this.booksRepository.findById(id).orElse(null);
		
		List<ReadingBookRecord> list = this.readingsRepository.getAllByBookOrderById(book); 
		
		for (ReadingBookRecord readingBookRecord : list) {
			readingBookRecord.setBook(null);
		}
		this.readingsRepository.saveAll(list); 
		
		this.booksRepository.deleteById(id);
		
		log.info("Book was removed by id: " + id);
		
		return "redirect:/books";
	}
}
