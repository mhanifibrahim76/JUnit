package pojo;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ProgressTest {

    @Test
    public void testProgressGettersAndSetters() {
        // Create a Progress object
        Progress progress = new Progress();

        // Set properties using setters
        Integer progressId = 1;
        Courses course = new Courses(); // Buat objek Courses sebagai contoh
        Users user = new Users(); // Buat objek Users sebagai contoh
        Integer progressPercentage = 75;
        Date lastAccessed = new Date();

        progress.setProgressId(progressId);
        progress.setCourses(course);
        progress.setUsers(user);
        progress.setProgressPercentage(progressPercentage);
        progress.setLastAccessed(lastAccessed);

        // Test getters
        assertEquals(progressId, progress.getProgressId());
        assertEquals(course, progress.getCourses());
        assertEquals(user, progress.getUsers());
        assertEquals(progressPercentage, progress.getProgressPercentage());
        assertEquals(lastAccessed, progress.getLastAccessed());
    }

    @Test
    public void testConstructorWithLastAccessed() {
        // Test the constructor that takes lastAccessed
        Date lastAccessed = new Date();
        Progress progress = new Progress(lastAccessed);

        assertEquals(lastAccessed, progress.getLastAccessed());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Test the constructor with all fields
        Courses course = new Courses(); // Buat objek Courses sebagai contoh
        Users user = new Users(); // Buat objek Users sebagai contoh
        Integer progressPercentage = 80;
        Date lastAccessed = new Date();

        Progress progress = new Progress(course, user, progressPercentage, lastAccessed);

        assertEquals(course, progress.getCourses());
        assertEquals(user, progress.getUsers());
        assertEquals(progressPercentage, progress.getProgressPercentage());
        assertEquals(lastAccessed, progress.getLastAccessed());
    }
}
