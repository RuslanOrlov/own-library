package mybooks.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mybooks.UriChain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import mybooks.models.User;

/**
 * @author Ruslan G. Orlov
 * This class configures the security settings of the application.
 * 
 */
@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	@PersistenceContext
	EntityManager entityManager;
	private UriChain uriChain;
	
	public SecurityConfig(UriChain uriChain) {
		this.uriChain = uriChain;
	}
	
	@Bean
	public LogoutHandler customLogoutHandler() {
		return new LogoutHandler() {
			@Override
			public void logout(HttpServletRequest request, 
								HttpServletResponse response, 
								Authentication authentication) {
				uriChain.clearAllUserUri((User) authentication.getPrincipal());
			}
		};
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) 
					throws UsernameNotFoundException {
				
				Query query = entityManager.createQuery("select u from User u "
													+ "left join fetch u.userAuthorities ua "
													+ "where u.username = :username");
				query.setParameter("username", username);
				User user = null;
				try {
					user = (User) query.getSingleResult();
				} catch (Exception e) {
					log.error("User '" + username + "' not found. Instead of it, '" 
									+ user + "' was returned!");
				}
				
				if (user != null) 
					return user; 
				throw new UsernameNotFoundException("User '" + username + "' not found");
			}
		};
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests()
					.requestMatchers("/authors/**", "/funds/**", "/genres/**", 
								"/books/**", "/readings/**").hasRole("VIEW")
					.requestMatchers("/", "/**").permitAll()
				.and()
					.csrf()
				.and()
					.formLogin()
						.loginPage("/login")
						.loginProcessingUrl("/authenticate")
				.and()
//					.sessionManagement()
//					.invalidSessionUrl("/")
//					.maximumSessions(1)
//					.maxSessionsPreventsLogin(true)
//					.expiredUrl("/")
//				.and().and()
					.logout()
						.logoutUrl("/my-logout")
						.addLogoutHandler(this.customLogoutHandler())
						.logoutSuccessUrl("/")
				.and()
					.build(); 
	}
}
