package mybooks.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.ReadingBookRecord;
import mybooks.models.TemporaryUserForRegistration;
import mybooks.models.User;
import mybooks.models.UserSettings;
import mybooks.repositories.AppThemesRepository;
import mybooks.repositories.ReadingBookRecordsRepository;
import mybooks.repositories.UserAuthorityTypesRepository;
import mybooks.repositories.UsersRepository;

/**
 * @author Ruslan G. Orlov
 * This class controls users accounts 
 * and their authorities by the administrator.
 * 
 */
@Slf4j
@Controller
@SessionAttributes("user")
@RequestMapping("/users")
public class AdminController {
	
	private UsersRepository usersRepository;
	private UserAuthorityTypesRepository userAuthorityTypesRepository;
	private ReadingBookRecordsRepository readingsRepository;
	private AppThemesRepository themesRepository;
	private PasswordEncoder passwordEncoder;
	private MyServicesClass service;
	private UriChain uriChain;
	
	private boolean isPageable = false;
	
	private int curPage  = 0;
	
	public AdminController(UsersRepository usersRepository, 
							UserAuthorityTypesRepository userAuthorityTypesRepository, 
							ReadingBookRecordsRepository readingsRepository, 
							AppThemesRepository themesRepository, 
							PasswordEncoder passwordEncoder, 
							MyServicesClass service, 
							UriChain uriChain) {
		this.usersRepository = usersRepository;
		this.userAuthorityTypesRepository = userAuthorityTypesRepository;
		this.readingsRepository = readingsRepository;
		this.themesRepository = themesRepository;
		this.passwordEncoder = passwordEncoder;
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
	
	@ModelAttribute(name = "user")
	public User user() {
		return new User();
	}
	
	public int getTotalPages() {
		if (this.usersRepository.count() % this.service.pageSize() == 0)
			return (int) this.usersRepository.count() / this.service.pageSize() - 1;
		return (int) this.usersRepository.count() / this.service.pageSize(); 
	}
	
	@ModelAttribute(name = "totalUsers")
	public Long getTotalUsers() {
		return this.usersRepository.count();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public String users(Model model, HttpServletRequest httpServletRequest) {
		
		if (isPageable) {
			Pageable pageable = PageRequest.of(this.curPage, this.service.pageSize());
			model.addAttribute("users", 
					this.usersRepository.getAllByIdGreaterThanEqual(1L, pageable));
			model.addAttribute("curPage", this.curPage + 1);
			model.addAttribute("totalPages", getTotalPages() + 1);
			model.addAttribute("isPageable", this.isPageable);
		} else {
			model.addAttribute("users", 
					this.usersRepository.findAll());
			model.addAttribute("isPageable", this.isPageable);
		}
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "users";
	}
	
	@GetMapping("/pageable")
	public String pageableSwitcher() {
		this.isPageable = !this.isPageable;
		this.uriChain.setLogging(false);
		return "redirect:/users";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		this.curPage = 0;
		this.uriChain.setLogging(false);
		return "redirect:/users"; 
	}
	
	@GetMapping("/prev")
	public String prevPage( ) {
		if (this.curPage > 0)
			this.curPage--;
		this.uriChain.setLogging(false);
		return "redirect:/users"; 
	}
	
	@GetMapping("/next")
	public String nextPage( ) {
		if (this.curPage < getTotalPages())
			this.curPage++;
		this.uriChain.setLogging(false);
		return "redirect:/users"; 
	}

	@GetMapping("/last")
	public String lastPage() {
		this.curPage = getTotalPages();
		this.uriChain.setLogging(false);
		return "redirect:/users"; 
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public String viewUser(@PathVariable long id, Model model, 
							HttpServletRequest httpServletRequest) {
		User user = this.usersRepository.findById(id).orElse(null);
		if (user == null) 
			user = new User("No user found", null, null, null, null, null); 
		model.addAttribute("user", user);
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-user";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/new")
	public String openCreateForm(@ModelAttribute("temporaryUser") 
								TemporaryUserForRegistration temporaryUser, 
								HttpServletRequest httpServletRequest) {
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "new-user";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	public String createUser(@Valid @ModelAttribute("temporaryUser") 
								TemporaryUserForRegistration temporaryUser, 
								Errors errors) {
		if (errors.hasErrors())
			return "new-user";
		
		User user = temporaryUser.transformToUser(this.passwordEncoder, null);
		user.setSettings(new UserSettings(this.themesRepository.findById(1L).orElse(null)));
		this.usersRepository.save(user);
		
		log.info("User was created by admin: {}", user);
		
		return "redirect:/users";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}/edit")
	public String openEditForm(@PathVariable long id, Model model,
								HttpServletRequest httpServletRequest) {
		User user = this.usersRepository.findById(id).orElse(null);
		if (user == null)
			return "redirect:/users";
		model.addAttribute("temporaryUser", user.transformToTemporaryUser());
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-user";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/update")
	public String updateUser(@Valid @ModelAttribute("temporaryUser") 
								TemporaryUserForRegistration temporaryUser, 
								Errors errors) { 
		if (errors.hasErrors())
			return "edit-user";
		
		User user = this.usersRepository.findById(temporaryUser.getId()).orElse(null);
		user = this.usersRepository.save(temporaryUser.transformToUser(null, user));
		
		log.info("User was updated by admin: {}", user);
		
		return "redirect:/users";
	}
	
	@GetMapping("/{id}/password")
	public String openChangePasswordForm(@PathVariable Long id, Model model) {
		User user = this.usersRepository.findById(id).orElse(null);
		
		if (user == null)
			return "redirect:/users";
		
		TemporaryUserForRegistration temporaryUser = user.transformToTemporaryUser();
		temporaryUser.preparingForChangePassword();
		
		model.addAttribute("temporaryUser", temporaryUser);
		return "change-password";
	}
	
	@PostMapping("/password")
	public String changePassword(@Valid @ModelAttribute("temporaryUser") 
								TemporaryUserForRegistration temporaryUser, 
								Errors errors) {
		if (errors.hasErrors())
			return "change-password";
		
		User user = this.usersRepository.findById(temporaryUser.getId()).orElse(null);
		this.usersRepository.save(temporaryUser.transformToUser(passwordEncoder, user));
		
		log.info("User password was changed: " + user.getUsername());
		
		String returnedRedirect = this.uriChain.getCurrentUri(this.service.currentUser());
		if (returnedRedirect == null)
			returnedRedirect = "redirect:/users";
		else
			returnedRedirect = "redirect:" + returnedRedirect;
		
		return returnedRedirect;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}/roles")
	public String openUserRolesForm(@PathVariable Long id, Model model,
								HttpServletRequest httpServletRequest) {
		User user = this.usersRepository.findById(id).orElse(null);
		if (user == null)
			return "redirect:/users";
		
		model.addAttribute("user", user);
		model.addAttribute("userAuthorityTypes", 
							this.userAuthorityTypesRepository.findAll());
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "user-roles";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/roles")
	public String updateUserRoles(User user, SessionStatus sessionStatus) {
		
		this.usersRepository.save(user);
		sessionStatus.setComplete();
		
		log.info("User '" + user.getUsername() + "' roles was updated: {}", 
							user.getUserAuthorities());
		
		return "redirect:/users";
	}
	
	@GetMapping("/{id}/removal_request")
	public String removalRequest(@PathVariable long id, Model model) {
		model.addAttribute("isId", 			true);
		model.addAttribute("id", 			id);
		model.addAttribute("title", 		"Запрос на подтверждение удаления!");
		model.addAttribute("message", 		"Вы действительно хотите удалить выбранного "
											+ "пользователя? При удалении пользователя "
											+ "одновременно будут очищены записи всех "
											+ "прочитанных им книг в разделе 'Мои книги'! "
											+ "Если удаление преждевременно, вы можете "
											+ "перейти к его редактированию.");
		model.addAttribute("actionUrl_1", 	"/users");
		model.addAttribute("action_1", 		"/remove");
		model.addAttribute("button_1", 		"Да");
		model.addAttribute("actionUrl_2", 	"/users");
		model.addAttribute("action_2", 		"/edit");
		model.addAttribute("button_2", 		"Редактировать");
		model.addAttribute("listRootUrl", 	"/users");
		model.addAttribute("button_cancel", "Нет");
		return "request-form";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}/remove")
	public String removeUser(@PathVariable long id) {
		User user = this.usersRepository.findById(id).orElse(null);
		
		List<ReadingBookRecord> list = this.readingsRepository.getAllByUserOrderById(user); 
		if (!list.isEmpty())
			this.readingsRepository.deleteAll(list);
		
		this.usersRepository.deleteById(id);
		
		log.info("User was removed by id: " + id);
		
		return "redirect:/users";
	}
}
