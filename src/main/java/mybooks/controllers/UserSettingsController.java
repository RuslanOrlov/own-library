package mybooks.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.UriChain;
import mybooks.models.AppTheme;
import mybooks.models.UserSettings;
import mybooks.repositories.AppThemesRepository;
import mybooks.repositories.UsersRepository;

/**
 * @author Ruslan G. Orlov
 * This class provides the user the 
 * possibility himself to control own settings.
 * 
 */
@Slf4j
@Controller
@RequestMapping("/settings")
public class UserSettingsController {
	
	private UsersRepository usersRepository;
	private AppThemesRepository themesRepository;
	private MyServicesClass service;
	private UriChain uriChain;
	
	public UserSettingsController(
						UsersRepository usersRepository, 
						AppThemesRepository themesRepository, 
						MyServicesClass service, 
						UriChain uriChain) {
		this.usersRepository = usersRepository;
		this.themesRepository = themesRepository;
		this.service = service;
		this.uriChain = uriChain;
	}
	
	@ModelAttribute(name = "appThemes")
	public Iterable<AppTheme> appThemes() {
		return this.themesRepository.findAll();
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
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public String viewCurrentUserSettings(Model model, HttpServletRequest httpServletRequest) {
		model.addAttribute("user", this.service.currentUser());
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "view-user-settings";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/edit")
	public String openEditForm(Model model, HttpServletRequest httpServletRequest) {
		model.addAttribute("settings", this.service.currentUser().getSettings());
		
		if (this.uriChain.isLogging())
			this.uriChain.addNewItem(this.service.currentUser(), 
								httpServletRequest.getRequestURI());
		else this.uriChain.setLogging(true);
		
		return "edit-user-settings";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public String updateCurrentUserSettings(@Valid @ModelAttribute("settings") 
										UserSettings userSettings, Errors errors) {
		if (errors.hasErrors())
			return "edit-user-settings";
		
		this.service.currentUser().setSettings(userSettings);
		this.usersRepository.save(this.service.currentUser());		
		this.uriChain.setLogging(false);
		
		log.info("User settings for user '" + this.service.currentUserName() + 
					"' was updated: {}", this.service.currentUser().getSettings());
		
		return "redirect:/settings";
	}
}
