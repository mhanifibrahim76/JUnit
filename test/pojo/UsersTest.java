package pojo;

import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UsersTest {

    @Test
    public void testUsersGettersAndSetters() {
        // Create a Users object
        Users user = new Users();

        // Set properties using setters
        Integer id = 1;
        String username = "testUser";
        String email = "test@example.com";
        String password = "password";
        String confirmPassword = "password"; // Confirm password test
        Date createdAt = new Date();
        Set progresses = new HashSet();
        Set commentses = new HashSet();
        Set purchasedCourses = new HashSet();

        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmPassword(confirmPassword); // Set confirmPassword
        user.setCreatedAt(createdAt);
        user.setProgresses(progresses);
        user.setCommentses(commentses);
        user.setPurchasedCourses(purchasedCourses);

        // Test getters
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(confirmPassword, user.getConfirmPassword()); // Test confirmPassword
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(progresses, user.getProgresses());
        assertEquals(commentses, user.getCommentses());
        assertEquals(purchasedCourses, user.getPurchasedCourses());
    }

    @Test
    public void testConstructorWithBasicFields() {
        // Test the constructor that takes basic fields
        String username = "testUser";
        String email = "test@example.com";
        String password = "password";
        Date createdAt = new Date();

        Users user = new Users(username, email, password, createdAt);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Test the constructor with all fields
        String username = "testUser";
        String email = "test@example.com";
        String password = "password";
        Date createdAt = new Date();
        Set progresses = new HashSet();
        Set commentses = new HashSet();
        Set purchasedCourses = new HashSet();

        Users user = new Users(username, email, password, createdAt, progresses, commentses, purchasedCourses);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(progresses, user.getProgresses());
        assertEquals(commentses, user.getCommentses());
        assertEquals(purchasedCourses, user.getPurchasedCourses());
    }
}
