package mybooks.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import mybooks.models.UserAuthorityTypes.AuthorityType;

public class UserTest {

    @Test
    public void testGetUsername() {
        User user = new User("johndoe", "password", "John Doe", 
        					LocalDate.now(), "123-456-7890", 
        					new ArrayList<UserAuthorityTypes>());
        assertNotNull(user.getUsername());
        assertEquals("johndoe", user.getUsername());
    }

    @Test
    public void testGetAuthorities() {
        UserAuthorityTypes userAuthority = 
        		new UserAuthorityTypes(1L, AuthorityType.ROLE_ADMIN, "this is test role");
        List<UserAuthorityTypes> userAuthorities = new ArrayList<>();
        userAuthorities.add(userAuthority);
        User user = new User("johndoe", "password", "John Doe", 
        					LocalDate.now(), "123-456-7890", userAuthorities);
        assertNotNull(user.getAuthorities());
        assertEquals(1, user.getAuthorities().size());
        assertEquals("ROLE_ADMIN", ((SimpleGrantedAuthority) 
        						user.getAuthorities().iterator().next()).getAuthority());
    }

    @Test
    public void testTransformToTemporaryUser() {
        User user = new User("johndoe", "password", "John Doe", 
        					LocalDate.now(), "123-456-7890", 
        					new ArrayList<UserAuthorityTypes>());
        user.setId(1L);
        TemporaryUserForRegistration temporaryUser = user.transformToTemporaryUser();
        assertNotNull(temporaryUser);
        assertEquals(1L, temporaryUser.getId());
        assertEquals("johndoe", temporaryUser.getUsername());
        assertEquals("password", temporaryUser.getPassword());
        assertEquals("password", temporaryUser.getConfirm());
        assertEquals("John Doe", temporaryUser.getFullname());
        assertEquals(LocalDate.now(), temporaryUser.getBirthDate());
        assertEquals("123-456-7890", temporaryUser.getPhoneNumber());
    }
}
