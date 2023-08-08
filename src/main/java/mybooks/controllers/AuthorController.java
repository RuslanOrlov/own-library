package mybooks.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.Author;
import mybooks.models.Book;
import mybooks.repositories.AuthorsRepository;
import mybooks.repositories.BooksRepository;

/**
 * @author Ruslan G. Orlov
 * This Controller controls the authors of books.
 * 
 */
@Slf4j
@Controller
@RequestMapping("/authors")
public class AuthorController {
	
	private AuthorsRepository authorsRepository;
	private BooksRepository booksRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	private boolean isReferencesInBooks = false;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public AuthorController(AuthorsRepository authorsRepository, 
							BooksRepository booksRepository, 
							MyServicesClass service, 
							UriChain uriChain) {
		this.authorsRepository = authorsRepository;
		this.booksRepository = booksRepository;
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
	
	public int getTotalPages() {
		int count = (int) this.authorsRepository.count(); 
		
		if (count == 0) 
			return count; 
		
		if (count % this.service.pageSize() == 0)
			return count / this.service.pageSize() - 1;
		
		return count / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalAuthors")
	public Long getTotalAuthors() {
		return authorsRepository.count();
	}
	
	@GetMapping
	public String authors(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("authors", 
					authorsRepository.getAllByIdGreaterThanEqual(1L, pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("authors", 
					authorsRepository.findAll());
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "authors";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/authors";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/authors"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/authors"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/authors"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/authors"; 
	}
	
	@GetMapping("/{id}")
	public String viewAuthor(@PathVariable long id, Model model, 
							HttpServletRequest httpServletRequest) {
		Author author = authorsRepository.findById(id).orElse(null);
		if (author == null) 
			author = new Author(null, "No author found", null);
		model.addAttribute("author", author);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-author";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/new")
	public String openCreateForm(Author author, 
								HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "new-author";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/create")
	public String createAuthor(@Valid Author author, Errors errors) {
		if (errors.hasErrors())
			return "new-author";
		
		authorsRepository.save(author);
		
		log.info("Author was created: {}", author);
		
		return "redirect:/authors";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/{id}/edit")
	public String openEditForm(@PathVariable long id, Model model,
								HttpServletRequest httpServletRequest) {
		Author author = authorsRepository.findById(id).orElse(null);
		if (author == null)
			return "redirect:/authors";
		model.addAttribute("author", author);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-author";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/update")
	public String updateAuthor(@Valid Author author, Errors errors) { 
		if (errors.hasErrors())
			return "edit-author";
		
		this.authorsRepository.save(author);
		
		log.info("Author was updated: {}", author);
		
		return "redirect:/authors";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable long id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранного автора?");
		model.addAttribute("actionUrl_1", 	"/authors");
		model.addAttribute("action_1", 		"/checkExistence");
		model.addAttribute("button_1", 		"Да");
		model.addAttribute("actionUrl_2", 	null);
		model.addAttribute("action_2", 		null);
		model.addAttribute("button_2", 		null);
		model.addAttribute("listRootUrl", 	"/authors");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@GetMapping("/{id}/checkExistence")
	public String checkAuthorExistence(@PathVariable long id, Model model) {

		// СНАЧАЛА: Проверяем наличие ссылок на авторов в книгах. И если 
		// такие ссылки есть, присваиваем соответствующему полю флага 
		// true и запрашиваем действие у пользователя.
		List<Book> books = new ArrayList<>();
		
		Author author = authorsRepository.findById(id).orElse(null);
		if (author != null)
			books = booksRepository.getAllByAuthorsContains(author);
		
		if (books.size() > 0) {
			this.isReferencesInBooks = true;
			model.addAttribute("isId", 			true);
			model.addAttribute("id", 			id);
			model.addAttribute("title", 		"Запрос на подтверждение удаления!");
			model.addAttribute("message", 		"Есть" + " (" + books.size() + ") " 
												+ "ссылка(и/ок) на удаляемого автора в книгах! "
												+ "Вы действительно хотите его удалить? Вместо "
												+ "удаления вы можете отредактировать автора.");
			model.addAttribute("actionUrl_1", 	"/authors");
			model.addAttribute("action_1", 		"/remove");
			model.addAttribute("button_1", 		"Удалить");
			model.addAttribute("actionUrl_2", 	"/authors");
			model.addAttribute("action_2", 		"/edit");
			model.addAttribute("button_2", 		"Редактировать");
			model.addAttribute("listRootUrl", 	"/authors");
			model.addAttribute("button_cancel", "Отменить");
			return "request-form";
		}
		
		// ЗАТЕМ: Если ссылок на автора в книгах нет, делаем редирект на удаление.
		String returnedRedirect = "redirect:/authors/" + id + "/remove";
		return returnedRedirect;
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/{id}/remove")
	public String removeAuthor(@PathVariable long id) {

		// СНАЧАЛА: Если в книгах есть ссылки на удаляемого автора (т.е. 
		// соответствующее поле флага = true), обновляем выборку книг с 
		// этим жанром и присваиваем ссылкам на него null.
		List<Book> books = new ArrayList<>();
		
		if (this.isReferencesInBooks) {
			Author author = authorsRepository.findById(id).orElse(null);
			if (author != null)
				books = booksRepository.getAllByAuthorsContains(author);
			for (Book book : books)
				book.getAuthors().remove(author);
			this.isReferencesInBooks = false;
		}
		
		// ЗАТЕМ: Безопасно удаляем автора (после предварительного 
		// "зануления" ссылок на него в книгах).
		authorsRepository.deleteById(id);
		
		log.info("Author was removed by id: " + id);
		
		return "redirect:/authors";
	}
}
