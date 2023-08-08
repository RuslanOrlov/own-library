package mybooks.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.Book;
import mybooks.models.ReadingBookRecord;
import mybooks.repositories.BooksRepository;
import mybooks.repositories.ReadingBookRecordsRepository;

/**
 * @author Ruslan G. Orlov
 * This class controls the read books list by the current user.
 * 
 */
@Slf4j
@Controller
@RequestMapping("/readings")
public class ReadingBookRecordController {
	
	private ReadingBookRecordsRepository readingsRepository; 
	private BooksRepository booksRepository;
	private MyServicesClass service; 
	private UriChain uriChain;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public ReadingBookRecordController(ReadingBookRecordsRepository readingsRepository, 
								BooksRepository booksRepository, 
								MyServicesClass service, 
								UriChain uriChain) {
		this.readingsRepository = readingsRepository; 
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
		int count = (int) this.readingsRepository.countByUser(this.service.currentUser()); 
		
		if (count == 0) 
			return count; 
		
		if (count % this.service.pageSize() == 0)
			return count / this.service.pageSize() - 1; 
		
		return count / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalReadingRecords")
	public Long getTotalReadingRecords() {
		return readingsRepository.countByUser(this.service.currentUser());
	}
	
	@ModelAttribute(name = "isNoReadings")
	public boolean isNoReadings() {
		List<ReadingBookRecord> list = this.readingsRepository.
				getAllByUserOrderById(this.service.currentUser()); 
		return list.isEmpty();
	}
	
	@GetMapping
	public String readings(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("records", 
					readingsRepository.getAllByIdNotNullAndUserEqualsOrderById(
										this.service.currentUser(), pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("records", 
					readingsRepository.getAllByUserOrderById(this.service.currentUser()));
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "readings";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/readings";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/readings"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/readings"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/readings"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/readings"; 
	}
	
	@GetMapping("/{id}/new")
	public String addReadingBook(@PathVariable long id, Model model) {
		
		String returnedUri = this.uriChain.getCurrentUri(this.service.currentUser());
		if (returnedUri == null)
			returnedUri = "/";
		this.uriChain.setLogging(false);
		
		Book book = this.booksRepository.findById(id).orElse(null); 
		
		if (book != null) {
			List<ReadingBookRecord> list = this.readingsRepository.
				getAllByUserAndBookAndIsReadingEndedIsFalse(this.service.currentUser(), book); 
			if (list.size() == 0) {
				ReadingBookRecord readingRecord = new ReadingBookRecord(); 
				readingRecord.setBook(book); 
				readingRecord.setUser(this.service.currentUser()); 
				this.readingsRepository.save(readingRecord); 
				log.info("Record of the book's reading by user was added: {}", readingRecord);
			} else {
				model.addAttribute("isId", 			false);
				model.addAttribute("id", 			0);
				model.addAttribute("title", 		"Недопустимая операция!");
				model.addAttribute("message", 		"Вы не можете взять эту книгу, "
													+ "так как она уже была выбрана ранее "
													+ "и еще не возвращена в библиотеку.");
				model.addAttribute("actionUrl_1", 	null);
				model.addAttribute("action_1", 		null);
				model.addAttribute("button_1", 		null);
				model.addAttribute("actionUrl_2", 	null);
				model.addAttribute("action_2", 		null);
				model.addAttribute("button_2", 		null);
				model.addAttribute("listRootUrl", 	returnedUri);
				model.addAttribute("button_cancel", "Закрыть");
				return "request-form";
			}
		}
		return "redirect:" + returnedUri; 
	}
	
	@GetMapping("/{id}/change_status")
	public String changeStatusReadingBook(@PathVariable long id) {
		ReadingBookRecord readingRecord = 
				this.readingsRepository.findById(id).orElse(null);
		
		if (readingRecord != null) {
			if (readingRecord.isReadingEnded()) {
				readingRecord.setReadingEnded(false);
				readingRecord.setEndDate(null);
			} else {
				readingRecord.setReadingEnded(true);
				readingRecord.setEndDate(LocalDateTime.now());
			}
			this.readingsRepository.save(readingRecord);
			log.info("Status of the book's reading was changed: {}", readingRecord);
		}
		
		this.uriChain.setLogging(false);
		return "redirect:/readings";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable long id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранную "
											+ "запись? Внимание, это необратимая операция!");
		model.addAttribute("actionUrl_1", 	"/readings");
		model.addAttribute("action_1", 		"/remove");
		model.addAttribute("button_1", 		"Да");
		model.addAttribute("actionUrl_2", 	null);
		model.addAttribute("action_2", 		null);
		model.addAttribute("button_2", 		null);
		model.addAttribute("listRootUrl", 	"/readings");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/{id}/remove")
	public String removeReadingBookRecord(@PathVariable long id) {
		this.readingsRepository.deleteById(id);
		
		log.info("Book's reading record for user '" + 
					this.service.currentUser().getUsername() + 
					"' was removed by id: " + id);
		
		return "redirect:/readings";
	}
	
	@GetMapping("/all_removal_request")
	public String allRemovalRequest(Model model) {
		model.addAttribute("isId", 			false);
		model.addAttribute("id", 			0);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите очистить (удалить) "
											+ "все записи? Внимание, это необратимая операция!");
		model.addAttribute("actionUrl_1", 	null);
		model.addAttribute("action_1", 		null);
		model.addAttribute("button_1", 		null);
		model.addAttribute("actionUrl_2", 	"/readings");
		model.addAttribute("action_2", 		"/all_remove");
		model.addAttribute("button_2", 		"Да");
		model.addAttribute("listRootUrl", 	"/readings");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/all_remove")
	public String removeAllReadingBookRecords() {
		List<ReadingBookRecord> list = this.readingsRepository.
				getAllByUserOrderById(this.service.currentUser());
		
		if (!list.isEmpty()) {
			this.readingsRepository.deleteAll(list);
			log.info("All books' reading records for user '" + 
						this.service.currentUser().getUsername() + "' were removed");
		}
		
		this.uriChain.setLogging(false);
		return "redirect:/readings";
	}

	@GetMapping("/last_removal_request")
	public String lastRemovalRequest(Model model) {
		model.addAttribute("isId", 			false);
		model.addAttribute("id", 			0);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите очистить (удалить) "
											+ "последнюю запись? Внимание, это необратимая "
											+ "операция!");
		model.addAttribute("actionUrl_1", 	null);
		model.addAttribute("action_1", 		null);
		model.addAttribute("button_1", 		null);
		model.addAttribute("actionUrl_2", 	"/readings");
		model.addAttribute("action_2", 		"/last_remove");
		model.addAttribute("button_2", 		"Да");
		model.addAttribute("listRootUrl", 	"/readings");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@PreAuthorize("hasRole('DELETE')")
	@GetMapping("/last_remove")
	public String removeLastReadingBookRecord() {
		List<ReadingBookRecord> list = this.readingsRepository.
				getAllByUserOrderById(this.service.currentUser());
		
		if (!list.isEmpty()) {
			log.info("The last book's reading record for user '" + 
						this.service.currentUser().getUsername() + 
						"' is being removed by id: " + 
						list.get(list.size() - 1).getId());
			this.readingsRepository.delete(list.get(list.size() - 1));
		}
		
		this.uriChain.setLogging(false);
		return "redirect:/readings";
	}
}
