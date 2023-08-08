package mybooks.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AppThemeTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 1L;
        String themeFileName = "theme.css";
        String themeName = "Default";
        AppTheme appTheme = new AppTheme(id, themeFileName, themeName);

        // Act
        Long actualId = appTheme.getId();
        String actualThemeFileName = appTheme.getThemeFileName();
        String actualThemeName = appTheme.getThemeName();

        // Assert
        assertEquals(id, actualId);
        assertEquals(themeFileName, actualThemeFileName);
        assertEquals(themeName, actualThemeName);
    }
}

