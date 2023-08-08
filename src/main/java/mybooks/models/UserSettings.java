package mybooks.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the user's own settings. 
 * 
 */
@Entity
@Data
@Table(name = "users_settings")
@NoArgsConstructor(force = true)
public class UserSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Min(value = 1, message = "Размер страницы не может быть < 1 записи!")
	@Max(value = 25, message = "Размер страницы не может быть > 25 записей!")
	private int pageSize = 5; 
		
	@ManyToOne
	private AppTheme appTheme;
	
	/**
	 * Аннотация @EqualsAndHashCode.Exclude  требуется,  чтобы  после 
	 * настройки блока "sessionManagement()" в объекте "HttpSecurity" 
	 * конфигурации  безопасности  приложения, во время аутентификаци 
	 * пользователя  в приложении, не  возникало  исключения (ошибки) 
	 * "StackOverflowException",  которое в  данном случае  возникает 
	 * из-за наличия в классах "User" и "UserSettings" ссылок друг на 
	 * друга, что приводило к бесконечному циклу взаимных обращений. 
	 * ----------------------------------------------------------------
	 * Аннотация @ToString.Exclude  требуется,  чтобы  после добавления 
	 * логгера  "lombok.extern.slf4j.Slf4j"  в классы контроллеров, при 
	 * выполнении  операций  записи  логирующих  сообщений  (к примеру, 
	 * "log.info("какой-то текст ... {}", anObject);"),    в    которых 
	 * присутствовали  объекты  "User" и "UserSettings",  не  возникало 
	 * исключения (ошибки) "StackOverflowException",  которое в  данном 
	 * случае возникает из-за наличия в классах "User" и "UserSettings" 
	 * ссылок друг на друга, что создавало бесконечный цикл обращений. 
	 * */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToOne(mappedBy = "settings")
	private User user;
	
	public UserSettings(AppTheme appTheme) {
		this.appTheme = appTheme;
	}
	
	public String getThemeFileName() {
		return this.getAppTheme().getThemeFileName();
	}
	
	public String getThemeName() {
		return this.getAppTheme().getThemeName();
	}
}
