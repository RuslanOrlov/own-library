package mybooks.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Ruslan G. Orlov
 * This class is a helper for creating a user 
 * account, updating it, and changing its password.
 * 
 */
@Data
public class TemporaryUserForRegistration {
	
	private Long id = 0L;
	
	@NotBlank(message = "Имя пользователя обязательно!")
	@Size(min = 4, message = "Должно быть не < 4 символов!")
	private String username; 
	
	@NotBlank(message = "Пароль обязателен!")
	@Size(min = 6, message = "Должно быть не < 6 символов!")
	private String password; 
	
	@NotBlank(message = "Подтверждение пароля обязательно!")
	@Size(min = 6, message = "Должно быть не < 6 символов!")
	private String confirm;
	
	@NotBlank(message = "Полное имя обязательно!")
	private String fullname; 
	
	@NotNull(message = "Дата рождения обязательна!")
	private LocalDate birthDate; 
	
	@NotBlank(message = "Телефон обязателен!")
	@Size(min = 5, max = 11, message = "Должно быть от 5 до 11 числовых символов!")
	@Digits(integer = 11, fraction = 0, message = "Номер может включать до 11 цифр.")
	private String phoneNumber; 
	
	private List<UserAuthorityTypes> userAuthorities = new ArrayList<>(); 
	
	private LocalDateTime createDate;
	
	public User transformToUser(PasswordEncoder passwordEncoder, User user) {
		
		// Создание нового пользователя
		if (passwordEncoder != null && user == null)
			user = new User(this.username, passwordEncoder.encode(this.password), 
						this.fullname, this.birthDate, this.phoneNumber, 
						this.userAuthorities);
		
		// Редактирование пользователя
		if (passwordEncoder == null && user != null) {
			if (!this.fullname.equals(user.getFullname()))
				user.setFullname(this.fullname);
			if (!this.birthDate.equals(user.getBirthDate()))
				user.setBirthDate(this.birthDate);
			if (!this.phoneNumber.equals(user.getPhoneNumber()))
				user.setPhoneNumber(this.phoneNumber);
		}
		
		// Изменение пароля пользователя
		if (passwordEncoder != null && user != null)
			user.setPassword(passwordEncoder.encode(this.password));
		
		return user;
	}
	
	public TemporaryUserForRegistration preparingForChangePassword() {
		
		// Данный метод  -  это "костыль" на тот случай,  если  пользователь, 
		// которому требуется изменить пароль, не  удовлетворяет  требованиям 
		// по заполнению  обязательных  полей  (полное  имя,  дата  рождения, 
		// номер телефона). В этом случае указанные поля  временно  инициали-
		// зируются формальными значениями, которые не будут сохранены в  БД, 
		// а  нужны  только лишь для обеспечения успешной  проверки  промежу-
		// точного объекта TemporaryUserForRegistration при изменении пароля.
		
		if (this.getFullname() == null || 
				this.getFullname().isBlank()) 
			this.setFullname("*");
		if (this.getBirthDate() == null)
			this.setBirthDate(LocalDate.now());
		if (this.getPhoneNumber() == null || 
				this.getPhoneNumber().isBlank())
			this.setPhoneNumber("00000");
		
		return this;
	}
}
