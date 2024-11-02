package DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Courses;
import pojo.PurchasedCourse;
import pojo.StudyRoomUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DAOPurchaseTest {

    private DAOPurchase daoPurchase;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void setUp() {
        sessionFactory = mock(SessionFactory.class);
        session = mock(Session.class);
        transaction = mock(Transaction.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        // Inject the mocked sessionFactory for testing
        StudyRoomUtil.setSessionFactory(sessionFactory);

        daoPurchase = new DAOPurchase();
    }

    @After
    public void tearDown() {
        StudyRoomUtil.setSessionFactory(null);
    }

    @Test
    public void testSavePurchasedCourse() {
        // Setup dummy PurchasedCourse data
        PurchasedCourse purchasedCourse = new PurchasedCourse();
        purchasedCourse.setId(1);
        purchasedCourse.setPurchaseDate(new Date());  // Dummy purchase date

        // Call the method to save purchased course
        daoPurchase.savePurchasedCourse(purchasedCourse);

        // Verifications
        verify(session).save(purchasedCourse);
        verify(transaction).commit();
        verify(session).close();

        // Assertions to ensure the data is saved correctly
        assertEquals("PurchasedCourse ID should be 1", 1, (int) purchasedCourse.getId());
        assertNotNull("Purchase date should not be null", purchasedCourse.getPurchaseDate());

        // Print message as proof
        System.out.println("PurchasedCourse with ID " + purchasedCourse.getId() + " saved successfully!");
    }

    @Test
    public void testSavePurchasedCourse_Exception() {
        // Setup dummy PurchasedCourse data
        PurchasedCourse purchasedCourse = new PurchasedCourse();
        purchasedCourse.setId(1);
        purchasedCourse.setPurchaseDate(new Date());

        // Simulasi exception saat session.save() dipanggil
        doThrow(new RuntimeException("Simulated Exception")).when(session).save(purchasedCourse);

        try {
            // Call the method to save purchased course
            daoPurchase.savePurchasedCourse(purchasedCourse);
            fail("Expected a RuntimeException to be thrown"); // Gagal jika tidak ada exception yang dilempar
        } catch (RuntimeException e) {
            // Assert the exception message
            assertEquals("Simulated Exception", e.getMessage());
        }

        // Pastikan rollback terjadi
        verify(transaction).rollback();
        // Pastikan sesi ditutup
        verify(session).close();

        // Pastikan objek purchasedCourse tidak berubah
        assertEquals("PurchasedCourse ID should still be 1 after exception", 1, (int) purchasedCourse.getId()); // Casting ke int
        assertNotNull("Purchase date should not be null", purchasedCourse.getPurchaseDate());

        // Print message for confirmation
        System.out.println("Exception occurred while saving PurchasedCourse. ID remains: " + purchasedCourse.getId());
    }

    @Test
    public void testGetCourseById_Success() {
        int courseId = 1;
        Courses course = new Courses();
        course.setCourseId(courseId);
        course.setTitle("Java Programming");  // Dummy course name
        course.setDescription("A comprehensive Java course.");  // Dummy description

        when(session.get(Courses.class, courseId)).thenReturn(course);

        Courses result = daoPurchase.getCourseById(courseId);

        assertEquals(course, result);
        verify(transaction).commit();
        verify(session).close();

        // Print message as proof
        System.out.println("Course with ID " + result.getCourseId() + " retrieved successfully: "
                + result.getTitle() + " - " + result.getDescription());
    }

    @Test
    public void testGetCourseById_Failure() {
        int courseId = 1;
        Courses course = new Courses();
        course.setCourseId(courseId);
        course.setTitle("Java Programming");  // Dummy course name
        course.setDescription("A comprehensive Java course.");  // Dummy description

        when(session.get(Courses.class, courseId)).thenThrow(new RuntimeException("Simulated Exception"));

        Courses result = daoPurchase.getCourseById(courseId);

        assertNull(result);

        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testGetPurchasedCoursesByUser_Success() {
        int userId = 1;
        List<PurchasedCourse> expectedCourses = new ArrayList<>();

        PurchasedCourse purchasedCourse = new PurchasedCourse();
        purchasedCourse.setId(1);
        purchasedCourse.setPurchaseDate(new Date());  // Dummy purchase date

        expectedCourses.add(purchasedCourse);

        Query query = mock(Query.class);
        when(session.createQuery("FROM PurchasedCourse WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.list()).thenReturn(expectedCourses);

        List<PurchasedCourse> result = daoPurchase.getPurchasedCoursesByUser(userId);

        assertEquals(expectedCourses, result);
        verify(session).close();

        // Print message as proof
        System.out.println("Purchased courses for user with ID " + userId + " retrieved successfully. "
                + "Total courses: " + result.size());
    }

    @Test
    public void testGetPurchasedCoursesByUser_Failure() {
        int userId = 1;
        List<PurchasedCourse> expectedCourses = new ArrayList<>();

        PurchasedCourse purchasedCourse = new PurchasedCourse();
        purchasedCourse.setId(1);
        purchasedCourse.setPurchaseDate(new Date());  // Dummy purchase date

        expectedCourses.add(purchasedCourse);

        Query query = mock(Query.class);
        when(session.createQuery("FROM PurchasedCourse WHERE users.id = :userId")).thenReturn(null);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.list()).thenReturn(expectedCourses);

        List<PurchasedCourse> result = daoPurchase.getPurchasedCoursesByUser(userId);

        assertNull(result);
        assertTrue(result.isEmpty());
        verify(session).close();
    }

}
