package pojo;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class CommentsTest {

    @Test
    public void testCommentsGettersAndSetters() {
        // Create a Comments object
        Comments comment = new Comments();

        // Set properties using setters
        Integer commentId = 1;
        Courses courses = new Courses();
        Users users = new Users(); // Pastikan Anda memiliki class Users
        String commentText = "Great course!";
        String username = "user123";
        Integer rating = 5;
        Date createdAt = new Date();

        comment.setCommentId(commentId);
        comment.setCourses(courses);
        comment.setUsers(users);
        comment.setComment(commentText);
        comment.setUsername(username);
        comment.setRating(rating);
        comment.setCreatedAt(createdAt);

        // Test getters
        assertEquals(commentId, comment.getCommentId());
        assertEquals(courses, comment.getCourses());
        assertEquals(users, comment.getUsers());
        assertEquals(commentText, comment.getComment());
        assertEquals(username, comment.getUsername());
        assertEquals(rating, comment.getRating());
        assertEquals(createdAt, comment.getCreatedAt());
    }

    @Test
    public void testConstructorWithCreatedAt() {
        // Test the constructor that only takes a createdAt date
        Date createdAt = new Date();
        Comments comment = new Comments(createdAt);

        assertEquals(createdAt, comment.getCreatedAt());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Test the constructor with all fields
        Courses courses = new Courses();
        Users users = new Users(); // Pastikan Anda memiliki class Users
        String commentText = "Great course!";
        String username = "user123";
        Integer rating = 5;
        Date createdAt = new Date();

        Comments comment = new Comments(courses, users, commentText, username, rating, createdAt);

        assertEquals(courses, comment.getCourses());
        assertEquals(users, comment.getUsers());
        assertEquals(commentText, comment.getComment());
        assertEquals(username, comment.getUsername());
        assertEquals(rating, comment.getRating());
        assertEquals(createdAt, comment.getCreatedAt());
    }
}
