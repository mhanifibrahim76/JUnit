package pojo;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CoursesTest {

    @Test
    public void testCoursesGettersAndSetters() {
        // Create a Courses object
        Courses course = new Courses();

        // Set properties using setters
        Integer courseId = 1;
        String title = "Java Programming";
        String description = "Learn Java from scratch";
        Set progresses = new HashSet(); // Buat objek Set untuk progresses
        Set commentses = new HashSet();  // Buat objek Set untuk commentses
        Set purchasedCourses = new HashSet(); // Buat objek Set untuk purchasedCourses

        course.setCourseId(courseId);
        course.setTitle(title);
        course.setDescription(description);
        course.setProgresses(progresses);
        course.setCommentses(commentses);
        course.setPurchasedCourses(purchasedCourses);

        // Test getters
        assertEquals(courseId, course.getCourseId());
        assertEquals(title, course.getTitle());
        assertEquals(description, course.getDescription());
        assertEquals(progresses, course.getProgresses());
        assertEquals(commentses, course.getCommentses());
        assertEquals(purchasedCourses, course.getPurchasedCourses());
    }

    @Test
    public void testConstructorWithTitle() {
        // Test the constructor that takes a title
        String title = "Java Programming";
        Courses course = new Courses(title);

        assertEquals(title, course.getTitle());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Test the constructor with all fields
        String title = "Java Programming";
        String description = "Learn Java from scratch";
        Set progresses = new HashSet(); 
        Set commentses = new HashSet(); 
        Set purchasedCourses = new HashSet(); 

        Courses course = new Courses(title, description, progresses, commentses, purchasedCourses);

        assertEquals(title, course.getTitle());
        assertEquals(description, course.getDescription());
        assertEquals(progresses, course.getProgresses());
        assertEquals(commentses, course.getCommentses());
        assertEquals(purchasedCourses, course.getPurchasedCourses());
    }
}
