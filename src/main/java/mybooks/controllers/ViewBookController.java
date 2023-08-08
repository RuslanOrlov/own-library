package mybooks.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.Book;
import mybooks.repositories.BooksRepository;

/**
 * @author Ruslan G. Orlov
 * This class controls the books of the library.
 *
 */
@Controller
@RequestMapping("/books")
public class ViewBookController {
	
	private BooksRepository booksRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public ViewBookController(BooksRepository booksRepository, 
								MyServicesClass service, 
								UriChain uriChain) {
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
		int count = (int) this.booksRepository.count(); 
		
		if (count == 0) 
			return count; 
		
		if (count % this.service.pageSize() == 0)
			return count / this.service.pageSize() - 1;
		
		return count / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalBooks")
	public Long getTotalBooks() {
		return booksRepository.count();
	}
	
	@GetMapping
	public String books(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("books", 
					booksRepository.getAllByIdGreaterThanEqual(1L, pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("books", 
					booksRepository.findAll());
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "books";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/books";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/books"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/books"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/books"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/books"; 
	}
	
	@GetMapping("/{id}")
	public String viewBook(@PathVariable long id, Model model, 
							HttpServletRequest httpServletRequest) {
		Book book = booksRepository.findById(id).orElse(null);
		if (book == null)
			book = new Book(null, "No book found", "No book found", 
							null, null, null, 0, null);
		model.addAttribute("book", book);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-book";
	}
	
}
