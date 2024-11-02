package pojo;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PurchasedCourseTest {

    @Test
    public void testPurchasedCourseGettersAndSetters() {
        // Create a PurchasedCourse object
        PurchasedCourse purchasedCourse = new PurchasedCourse();

        // Set properties using setters
        Integer id = 1;
        Courses course = new Courses(); // Buat objek Courses sebagai contoh
        Users user = new Users(); // Buat objek Users sebagai contoh
        Date purchaseDate = new Date();

        purchasedCourse.setId(id);
        purchasedCourse.setCourses(course);
        purchasedCourse.setUsers(user);
        purchasedCourse.setPurchaseDate(purchaseDate);

        // Test getters
        assertEquals(id, purchasedCourse.getId());
        assertEquals(course, purchasedCourse.getCourses());
        assertEquals(user, purchasedCourse.getUsers());
        assertEquals(purchaseDate, purchasedCourse.getPurchaseDate());
    }

    @Test
    public void testConstructorWithPurchaseDate() {
        // Test the constructor that takes purchaseDate
        Date purchaseDate = new Date();
        PurchasedCourse purchasedCourse = new PurchasedCourse(purchaseDate);

        assertEquals(purchaseDate, purchasedCourse.getPurchaseDate());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Test the constructor with all fields
        Courses course = new Courses(); // Buat objek Courses sebagai contoh
        Users user = new Users(); // Buat objek Users sebagai contoh
        Date purchaseDate = new Date();

        PurchasedCourse purchasedCourse = new PurchasedCourse(course, user, purchaseDate);

        assertEquals(course, purchasedCourse.getCourses());
        assertEquals(user, purchasedCourse.getUsers());
        assertEquals(purchaseDate, purchasedCourse.getPurchaseDate());
    }
}
