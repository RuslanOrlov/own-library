package mybooks.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mybooks.MyServicesClass;
import mybooks.models.TemporaryUserForRegistration;
import mybooks.models.User;
import mybooks.models.UserSettings;
import mybooks.models.UserAuthorityTypes.AuthorityType;
import mybooks.repositories.AppThemesRepository;
import mybooks.repositories.UserAuthorityTypesRepository;
import mybooks.repositories.UsersRepository;

/**
 * @author Ruslan G. Orlov
 * This class controls the registration of its own account by 
 * the user himself and redirects him after it to the login page. 
 * 
 */
@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationController {
	
	private UsersRepository usersRepository;
	private UserAuthorityTypesRepository userAuthoritiesRepositry;
	private AppThemesRepository themesRepository;
	private PasswordEncoder passwordEncoder;
	private MyServicesClass service;
	
	public RegistrationController(UsersRepository usersRepository, 
									UserAuthorityTypesRepository userAuthoritiesRepositry, 
									AppThemesRepository themesRepository, 
									PasswordEncoder passwordEncoder, 
									MyServicesClass service) {
		this.usersRepository = usersRepository;
		this.userAuthoritiesRepositry = userAuthoritiesRepositry;
		this.themesRepository = themesRepository;
		this.passwordEncoder = passwordEncoder;
		this.service = service;
	}
	
	@ModelAttribute(name = "theme")
	public String theme() {
		return this.service.themeApp();
	}
	
	@GetMapping
	public String registerForm(TemporaryUserForRegistration 
								temporaryUserForRegistration) {
		return "registration";
	}
	
	@PostMapping
	public String processRegistration(
					@Valid TemporaryUserForRegistration temporaryUserForRegistration, 
					Errors errors) {
		if (temporaryUserForRegistration.getUserAuthorities().isEmpty())
			temporaryUserForRegistration.getUserAuthorities().add(
					this.userAuthoritiesRepositry.findByAuthority(AuthorityType.ROLE_VIEW));
		
		if (errors.hasErrors())
			return "registration";
		
		User user = temporaryUserForRegistration.transformToUser(this.passwordEncoder, null);
		user.setSettings(new UserSettings(this.themesRepository.findById(1L).orElse(null)));
		this.usersRepository.save(user);
		
		log.info("User was created by him its own: {}", user);
		
		return "redirect:/login";
	}
}
