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
import mybooks.models.Fund;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.FundsRepository;

/**
 * @author Ruslan G. Orlov
 * This Controller controls the funds of the library.
 *
 */
@Slf4j
@Controller
@RequestMapping("/funds")
public class FundController {
	
	private FundsRepository fundsRepository;
	private BooksRepository booksRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	private boolean isReferencesInBooks = false;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public FundController(FundsRepository fundsRepository,
							BooksRepository booksRepository, 
							MyServicesClass service, 
							UriChain uriChain) {
		this.fundsRepository = fundsRepository;
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
		int count = (int) this.fundsRepository.count(); 
		
		if (count == 0) 
			return count; 
		
		if (count % this.service.pageSize() == 0)
			return count / this.service.pageSize() - 1;
		
		return count / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalFunds")
	public Long getTotalFunds() {
		return fundsRepository.count();
	}
	
	@GetMapping
	public String funds(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("funds", 
					fundsRepository.getAllByIdNotNull(pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("funds", 
					fundsRepository.findAll());
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "funds";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/funds";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/funds"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/funds"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/funds"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/funds"; 
	}
	
	@GetMapping("/{id}")
	public String viewFund(@PathVariable String id, Model model, 
							HttpServletRequest httpServletRequest) {
		Fund fund = fundsRepository.findById(id).orElse(null);
		if (fund == null)
			fund = new Fund(null, "No fund found", null);
		model.addAttribute("fund", fund);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-fund";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/new")
	public String openCreateForm(Fund fund, 
								HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "new-fund";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/create")
	public String createFund(@Valid Fund fund, Errors errors) {
		if (errors.hasErrors())
			return "new-fund";
		
		fundsRepository.save(fund);
		
		log.info("Fund was created: {}", fund);
		
		return "redirect:/funds";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@GetMapping("/{id}/edit")
	public String openEditForm(@PathVariable String id, Model model,
								HttpServletRequest httpServletRequest) {
		Fund fund = fundsRepository.findById(id).orElse(null);
		if (fund == null)
			return "redirect:/funds";
		model.addAttribute("fund", fund);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-fund";
	}
	
	@PreAuthorize("hasRole('ADD_EDIT')")
	@PostMapping("/update")
	public String updateFund(@Valid Fund fund, Errors errors) {
		if (errors.hasErrors())
			return "edit-fund";
		
		fundsRepository.save(fund);
		
		log.info("Fund was updated: {}", fund);
		
		return "redirect:/funds";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable String id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранный фонд?");
		model.addAttribute("actionUrl_1", 	"/funds");
		model.addAttribute("action_1", 		"/checkExistence");
		model.addAttribute("button_1", 		"Да");
		model.addAttribute("actionUrl_2", 	null);
		model.addAttribute("action_2", 		null);
		model.addAttribute("button_2", 		null);
		model.addAttribute("listRootUrl", 	"/funds");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@GetMapping("/{id}/checkExistence")
	public String checkFundExistence(@PathVariable String id, Model model) {

		// СНАЧАЛА: Проверяем наличие ссылок на фонд в книгах. И если 
		// такие ссылки есть, присваиваем соответствующему полю флага 
		// true и запрашиваем действие у пользователя.
		List<Book> books = new ArrayList<>();
		
		Fund fund = fundsRepository.findById(id).orElse(null);
		if (fund != null)
			books = booksRepository.getAllByFund(fund);
		
		if (books.size() > 0) {
			this.isReferencesInBooks = true;
			model.addAttribute("isId", 			true);
			model.addAttribute("id", 			id);
			model.addAttribute("title", 		"Запрос на подтверждение удаления!");
			model.addAttribute("message", 		"Есть" + " (" + books.size() + ") " 
												+ "ссылка(и/ок) на удаляемый фонд в книгах! "
												+ "Вы действительно хотите его удалить? Вместо "
												+ "удаления вы можете отредактировать фонд.");
			model.addAttribute("actionUrl_1", 	"/funds");
			model.addAttribute("action_1", 		"/remove");
			model.addAttribute("button_1", 		"Удалить");
			model.addAttribute("actionUrl_2", 	"/funds");
			model.addAttribute("action_2", 		"/edit");
			model.addAttribute("button_2", 		"Редактировать");
			model.addAttribute("listRootUrl", 	"/funds");
			model.addAttribute("button_cancel", "Отменить");
			return "request-form";
		}
		
		// ЗАТЕМ: Если ссылок на фонд в книгах нет, делаем редирект на удаление.
		String returnedRedirect = "redirect:/funds/" + id + "/remove";
		return returnedRedirect;
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/{id}/remove")
	public String removeFund(@PathVariable String id) {

		// СНАЧАЛА: Если в книгах есть ссылки на удаляемый фонд (т.е. 
		// соответствующее поле флага = true), обновляем выборку книг 
		// с этим фондом и присваиваем ссылкам на него null.
		List<Book> books = new ArrayList<>();
		
		if (this.isReferencesInBooks) {
			Fund fund = fundsRepository.findById(id).orElse(null);
			if (fund != null)
				books = booksRepository.getAllByFund(fund);
			for (Book book : books)
				book.setFund(null);
			this.isReferencesInBooks = false;
		}
		
		// ЗАТЕМ: Безопасно удаляем библиотечный фонд (после 
		// предварительного "зануления" ссылок на него в книгах).
		fundsRepository.deleteById(id);
		
		log.info("Fund was removed by id: " + id);
		
		return "redirect:/funds";
	}
}
