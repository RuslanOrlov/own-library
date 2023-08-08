package mybooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import mybooks.models.User;

/**
 * @author Ruslan G. Orlov
 * This Class provides a service managing the 
 * user's passages history in the application. 
 * 
 */
@Component
@Data
public class UriChain {
	
	private MyServicesClass service;
	
	private List<UserUri> list;
	
	private boolean isLogging;
	
	public UriChain(MyServicesClass service) {
		this.service = service;
		this.list = new ArrayList<>();
		this.isLogging = true;
	}
	
	public void clearAllUserUri(User user) {
		UserUri lastItem = this.getLastItem(user);
		while (lastItem != null) {
			UserUri deletingItem = lastItem;
			lastItem = lastItem.getPrevUri();
			this.list.removeAll(Arrays.asList(deletingItem));
		}
	}
	
	public int getAllItemsCount() {
		return this.getList().size();
	}
	
	public int getAllItemsCountOfCurrentUser() {
		int result = 0;
		
		if (this.service.isLoggedIn()) {
			UserUri lastItem = this.getLastItem(this.service.currentUser());
			if (lastItem != null)
				result = lastItem.getNumber();
		}
		
		return result;
	}
	
	public int getCurrentItemNumberOfCurrentUser() {
		int result = 0;
		
		if (this.service.isLoggedIn()) {
			UserUri currentItem = this.getCurrentItem(this.service.currentUser());
			if (currentItem != null)
				result = currentItem.getNumber();
		}
		
		return result;
	}
	
	public String currentStat(boolean isParenthesis) {
		String result = "" + this.getAllItemsCount() + " / " + 
							 this.getAllItemsCountOfCurrentUser() + " / " + 
							 this.getCurrentItemNumberOfCurrentUser();
		if (isParenthesis)
			return " ( " + result + " )";
		return " [ " + result + " ]";
	}
	
	public void addNewItem(User user, String uri) {
		UserUri lastItem = getLastItem(user);
		
		if (lastItem == null)
			this.list.add(new UserUri(user, uri, null, null, true, 1));
		else {
			UserUri newItem = new UserUri(user, uri, lastItem, null, true, 
											lastItem.getNumber() + 1);
			lastItem.setNextUri(newItem);
			lastItem.setCurrent(false);
			
			UserUri currentItem = getCurrentItem(user);
			if (currentItem != null)
				currentItem.setCurrent(false);
			
			this.list.add(newItem);
		}
	}
	
	public UserUri getLastItem(User user) {
		UserUri result = null;
		
		for (UserUri userUri : this.list)
			if ( userUri.getUser().equals(user) && 
				 userUri.getNextUri() == null ) {
				result = userUri;
				break;
			}
		
		return result;
	}
	
	public String getLastUri(User user) {
		String lastUri = null;
		
		UserUri lastItem = getLastItem(user);
		if (lastItem != null)
			lastUri = lastItem.getUri();
		
		return lastUri;
	}
	
	public UserUri getCurrentItem(User user) {
		UserUri result = null;
		
		for (UserUri userUri : this.list)
			if ( userUri.getUser().equals(user) && 
				 userUri.isCurrent() ) {
				result = userUri;
				break;
			}
		
		return result;
	}
	
	public String getCurrentUri(User user) {
		String currentUri = null;
		
		UserUri currentItem = getCurrentItem(user);
		if (currentItem != null)
			currentUri = currentItem.getUri();
		
		return currentUri;
	}
	
	public UserUri moveToPrevItem(User user) {
		this.setLogging(false);
		
		UserUri result = getCurrentItem(user);
		
		if (result != null && result.getPrevUri() != null) {
			result.setCurrent(false);
			result.getPrevUri().setCurrent(true);
			result = result.getPrevUri();
		}
		
		return result;
	}
	
	public UserUri moveToNextItem(User user) {
		this.setLogging(false);
		
		UserUri result = getCurrentItem(user);
		
		if (result != null && result.getNextUri() != null) {
			result.setCurrent(false);
			result.getNextUri().setCurrent(true);
			result = result.getNextUri();
		}
		
		return result;
	}
	
	@Data
	public class UserUri {
		private User user;	
		private String uri;
		private UserUri prevUri;
		private UserUri nextUri;
		private boolean isCurrent;
		private int number;
		
		UserUri() {
		}
		
		UserUri(User user, String uri, 
				UserUri prevUri, UserUri nextUri, 
				boolean isCurrent, int number) {
			this.user = user;
			this.uri = uri;
			this.prevUri = prevUri;
			this.nextUri = nextUri;
			this.isCurrent = isCurrent;
			this.number = number;
		}
	}
	
}
