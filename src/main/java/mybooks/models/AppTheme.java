package mybooks.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ruslan G. Orlov
 * This class represents the model for the application themes.
 * 
 */
@Entity
@Data
@Table(name = "app_themes")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AppTheme implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String themeFileName;
	private String themeName;
	
}
