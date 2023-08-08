package mybooks.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import mybooks.MyServicesClass;
import mybooks.UriChain;

/**
 * @author Ruslan G. Orlov
 * This class controls the showing of the application 
 * start page, the user login page, and also manages 
 * the passages of the current user along the application 
 * using his saved passages history in the application.
 * 
 */
@Controller
public class HomeController {
	
	private MyServicesClass service;
	private UriChain uriChain;
	
	public HomeController(MyServicesClass service, UriChain uriChain) {
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
	
	@GetMapping("/")
	public String home(HttpServletRequest httpServletRequest) {
		
		if (this.isLoggedIn())
			if (this.uriChain.isLogging())
				this.uriChain.addNewItem(this.service.currentUser(), 
					httpServletRequest.getRequestURI()); 
			else this.uriChain.setLogging(true);

		return "home";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "login-form";
	}
	
	@GetMapping("/prev")
	public String prev(HttpServletRequest httpServletRequest) {
		String returnedRedirect = null;
		
		if (this.isLoggedIn()) {
			this.uriChain.moveToPrevItem(this.service.currentUser());
			returnedRedirect = this.uriChain.getCurrentUri(this.service.currentUser());
			if (returnedRedirect == null)
				returnedRedirect = "redirect:" + httpServletRequest.getRequestURI();
			else
				returnedRedirect = "redirect:" + returnedRedirect;
		}
			
		return returnedRedirect;
	}
	
	@GetMapping("/next")
	public String next(HttpServletRequest httpServletRequest) {
		String returnedRedirect = null;
		
		if (this.isLoggedIn()) {
			this.uriChain.moveToNextItem(this.service.currentUser());
			returnedRedirect = this.uriChain.getCurrentUri(this.service.currentUser());
			if (returnedRedirect == null)
				returnedRedirect = "redirect:" + httpServletRequest.getRequestURI();
			else
				returnedRedirect = "redirect:" + returnedRedirect;
		}
			
		return returnedRedirect;
	}
	
}
