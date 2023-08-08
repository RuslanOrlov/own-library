package mybooks;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.Data;
import mybooks.models.User;
import mybooks.repositories.UsersRepository;

/**
 * @author Ruslan G. Orlov
 * This Class provides information about the current 
 * user account and everything related to it. 
 * 
 */
@Service
@Data
public class MyServicesClass {
	
	UsersRepository usersRepository;
	
	String leadNoteWithStat;
	String leadNoteWithoutStat;

	public MyServicesClass(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
		this.leadNoteWithStat = " << Current URI";
		this.leadNoteWithoutStat = "No Authenticated User";
	}
	
	public User currentUser() {
		Authentication authentication = 
				SecurityContextHolder.getContext().getAuthentication();
		return (User) authentication.getPrincipal();
	}
	
	public String currentUserName() {
		Authentication authentication = 
				SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
//	/* Another version of the isLoggedIn() method */
//	public boolean isLoggedIn() {
//		Authentication authentication = 
//					SecurityContextHolder.getContext().getAuthentication();
//		
//		if (authentication == null || 
//				AnonymousAuthenticationToken.class.
//				isAssignableFrom(authentication.getClass()))
//			return false;
//		
//		return authentication.isAuthenticated();
//	}
	
	public boolean isLoggedIn() {
		return SecurityContextHolder.getContext().getAuthentication().
				getPrincipal() instanceof User;
	}
	
	public Long currentUserId() {
		User user = this.usersRepository.findByUsername(this.currentUserName());
		
		if (user == null)
			return 0L;
		
		return user.getId();
	}
	
	public int pageSize() {
		int result = 5;
		
		if (this.isLoggedIn())
			if (this.currentUser().getSettings() != null)
				result = this.currentUser().getSettings().getPageSize();
		
		return result;
	}
	
	public String themeApp() {
		String result = "/styles.css";
		
		if (this.isLoggedIn())
			if (this.currentUser().getSettings() != null)
				result = this.currentUser().getSettings().getThemeFileName();
		
		return result;
	}
	
}
