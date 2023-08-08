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
import mybooks.models.Book;
import mybooks.models.Genre;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.GenresRepository;

/**
 * @author Ruslan G. Orlov
 * This Controller controls the genres of the books.
 *
 */
@Slf4j
@Controller
@RequestMapping("/genres")
public class GenreController {
	
	private GenresRepository genresRepository;
	private BooksRepository booksRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	private boolean isReferencesInBooks = false;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public GenreController(GenresRepository genresRepository,
							BooksRepository booksRepository, 
							MyServicesClass service, 
							UriChain uriChain) {
		this.genresRepository = genresRepository;
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
		int count = (int) this.genresRepository.count(); 
		
		if (count == 0) 
			return count; 
		
		if (count % this.service.pageSize() == 0)
			return count / this.service.pageSize() - 1;
		
		return count / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalGenres")
	public Long getTotalGenres() {
		return genresRepository.count();
	}
	
	@GetMapping
	public String genres(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("genres", 
					genresRepository.getAllByIdGreaterThanEqual(1L, pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("genres", 
					genresRepository.findAll());
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "genres";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/genres";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/genres"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/genres"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/genres"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/genres"; 
	}
	
	@GetMapping("/{id}")
	public String viewGenre(@PathVariable long id, Model model, 
							HttpServletRequest httpServletRequest) {
		Genre genre = genresRepository.findById(id).orElse(null);
		if (genre == null) 
			genre = new Genre(null, "Жанр не найден", "No genre found", null);
		model.addAttribute("genre", genre);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-genre";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/new")
	public String openCreateForm(Genre genre, 
								HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "new-genre";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/create")
	public String createGenre(@Valid Genre genre, Errors errors) {
		if (errors.hasErrors())
			return "new-genre";
		
		genresRepository.save(genre);
		
		log.info("Genre was created: {}", genre);
		
		return "redirect:/genres";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/{id}/edit")
	public String openEditForm(@PathVariable long id, Model model,
								HttpServletRequest httpServletRequest) {
		Genre genre = genresRepository.findById(id).orElse(null);
		if (genre == null)
			return "redirect:/genres";
		model.addAttribute("genre", genre);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-genre";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/update")
	public String updateGenre(@Valid Genre genre, Errors errors) { 
		if (errors.hasErrors())
			return "edit-genre";
		
		genresRepository.save(genre);
		
		log.info("Genre was updated: {}", genre);
		
		return "redirect:/genres";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable long id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранный жанр?");
		model.addAttribute("actionUrl_1", 	"/genres");
		model.addAttribute("action_1", 		"/checkExistence");
		model.addAttribute("button_1", 		"Да");
		model.addAttribute("actionUrl_2", 	null);
		model.addAttribute("action_2", 		null);
		model.addAttribute("button_2", 		null);
		model.addAttribute("listRootUrl", 	"/genres");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@GetMapping("/{id}/checkExistence")
	public String checkGenreExistence(@PathVariable long id, Model model) {

		// СНАЧАЛА: Проверяем наличие ссылок на жанр в книгах. И если 
		// такие ссылки есть, присваиваем соответствующему полю флага 
		// true и запрашиваем действие у пользователя.
		List<Book> books = new ArrayList<>();
		
		Genre genre = genresRepository.findById(id).orElse(null);
		if (genre != null)
			books = booksRepository.getAllByGenre(genre);
		
		if (books.size() > 0) {
			this.isReferencesInBooks = true;
			model.addAttribute("isId", 			true);
			model.addAttribute("id", 			id);
			model.addAttribute("title", 		"Запрос на подтверждение удаления!");
			model.addAttribute("message", 		"Есть" + " (" + books.size() + ") " 
												+ "ссылка(и/ок) на удаляемый жанр в книгах! "
												+ "Вы действительно хотите его удалить? Вместо "
												+ "удаления вы можете отредактировать жанр.");
			model.addAttribute("actionUrl_1", 	"/genres");
			model.addAttribute("action_1", 		"/remove");
			model.addAttribute("button_1", 		"Удалить");
			model.addAttribute("actionUrl_2", 	"/genres");
			model.addAttribute("action_2", 		"/edit");
			model.addAttribute("button_2", 		"Редактировать");
			model.addAttribute("listRootUrl", 	"/genres");
			model.addAttribute("button_cancel", "Отменить");
			return "request-form";
		}
		
		// ЗАТЕМ: Если ссылок на жанр в книгах нет, делаем редирект на удаление.
		String returnedRedirect = "redirect:/genres/" + id + "/remove";
		return returnedRedirect;
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/{id}/remove")
	public String removeGenre(@PathVariable long id) {
		
		// СНАЧАЛА: Если в книгах есть ссылки на удаляемый жанр (т.е. 
		// соответствующее поле флага = true), обновляем выборку книг 
		// с этим жанром и присваиваем ссылкам на него null.
		List<Book> books = new ArrayList<>();
		
		if (this.isReferencesInBooks) {
			Genre genre = genresRepository.findById(id).orElse(null);
			if (genre != null)
				books = booksRepository.getAllByGenre(genre);
			for (Book book : books)
				book.setGenre(null);
			this.isReferencesInBooks = false;
		}
		
		// ЗАТЕМ: Безопасно удаляем жанр (после предварительного 
		// "зануления" ссылок на него в книгах).
		genresRepository.deleteById(id);
		
		log.info("Genre was removed by id: " + id);
		
		return "redirect:/genres";
	}
}
