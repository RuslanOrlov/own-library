package mybooks.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the user 
 * account. It also converts the user account into 
 * a 'TemporaryUserForRegistration' helper class.
 * 
 */
@Entity
@Table(name = "users")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class User implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	private final String 	username; 
	private String 			password; 
	private String 			fullname; 
	private LocalDate 		birthDate; 
	private String 			phoneNumber; 
	
	@ManyToMany 
	private List<UserAuthorityTypes> userAuthorities = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL)
	private UserSettings settings; 
	
	private final LocalDateTime createDate 	= LocalDateTime.now(); 
	
	public User(String username, String password, 
				String fullname, LocalDate birthDate, 
				String phoneNumber, List<UserAuthorityTypes> userAuthorities, 
				UserSettings settings) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
		this.userAuthorities = userAuthorities;
		this.settings = settings;
	}
	
	public User(String username, String password, 
				String fullname, LocalDate birthDate, 
				String phoneNumber, List<UserAuthorityTypes> userAuthorities) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
		this.userAuthorities = userAuthorities;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
		
		for (UserAuthorityTypes userAuthority : this.userAuthorities) {
			grantedAuthorities.add(
					new SimpleGrantedAuthority(userAuthority.getAuthority().name()));
		}
		
		// Если у пользователя нет ни одной роли, ему предоставляется 
		// фиктивная роль 'ROLE_NOTHING'. Этот код можно исключить и 
		// возвращать пустой список ролей 'grantedAuthorities'. 
		if (grantedAuthorities.isEmpty())
			grantedAuthorities.add(
					new SimpleGrantedAuthority("ROLE_NOTHING"));
		
		return grantedAuthorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public TemporaryUserForRegistration transformToTemporaryUser() {
		TemporaryUserForRegistration temporaryUser = 
				new TemporaryUserForRegistration();
		
		temporaryUser.setId(this.getId());
		temporaryUser.setUsername(this.getUsername());
		temporaryUser.setPassword(this.getPassword());
		temporaryUser.setConfirm(this.getPassword());
		temporaryUser.setFullname(this.getFullname());
		temporaryUser.setBirthDate(this.getBirthDate());
		temporaryUser.setPhoneNumber(this.getPhoneNumber());
		temporaryUser.setUserAuthorities(this.getUserAuthorities());
		temporaryUser.setCreateDate(this.getCreateDate());
		
		return temporaryUser;
	}
}
