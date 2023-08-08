package mybooks.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import mybooks.MyServicesClass;
import mybooks.UriChain;

/**
 * @author Ruslan G. Orlov
 * This class controls the display of the application error page.
 * 
 */
@Controller
public class MyErrorController implements ErrorController {
	
	private MyServicesClass service;
	private UriChain uriChain;

	public MyErrorController(MyServicesClass service, UriChain uriChain) {
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
	
	@ModelAttribute(name = "curUriStat")
	public String curUriStat(HttpServletRequest httpServletRequest) {
		String value = null;
		
		if (this.isLoggedIn())
			value = httpServletRequest.getRequestURI() + this.service.getLeadNoteWithStat();
		else 
			value = this.service.getLeadNoteWithoutStat();
		
		return value + this.uriChain.currentStat(this.isLoggedIn());
	}
	
	@GetMapping("/error")
	public String handleError() {
		return "error";
	}
}
