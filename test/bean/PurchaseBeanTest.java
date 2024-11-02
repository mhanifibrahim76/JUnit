package bean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import DAO.DAOPurchase;
import pojo.Courses;
import pojo.PurchasedCourse;
import pojo.Users;

import java.util.Arrays;
import java.util.Date;

public class PurchaseBeanTest {

    @Mock
    private DAOPurchase daoPurchase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private PurchaseBean purchaseBean;
    private Users mockUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        purchaseBean = new PurchaseBean();
        purchaseBean.setDAOPurchase(daoPurchase);  // Inject mocked DAOPurchase

        // Create a mock user
        mockUser = new Users();
        mockUser.setId(1);

        // Mock the session and request
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(mockUser);
    }

    @Test
    public void testHasPurchasedCourseTrue() {
        // Set up mock purchased courses
        PurchasedCourse purchasedCourse = new PurchasedCourse();
        Courses course = new Courses();
        course.setCourseId(1); // Assign courseId to 1
        purchasedCourse.setCourses(course);
        purchasedCourse.setUsers(mockUser);
        purchasedCourse.setPurchaseDate(new Date());

        // Mock DAO response
        when(daoPurchase.getPurchasedCoursesByUser(mockUser.getId())).thenReturn(Arrays.asList(purchasedCourse));

        // Execute the method
        boolean result = purchaseBean.hasPurchasedCourse(1, request);

        // Verify the result and dummy data
        assertTrue("Expected user to have purchased course with ID 1", result);
        verify(daoPurchase, times(1)).getPurchasedCoursesByUser(mockUser.getId());
        assertEquals((Integer) 1, purchasedCourse.getCourses().getCourseId());  // Cast courseId to Integer
        assertEquals(mockUser, purchasedCourse.getUsers());
        
        // Print for verification
        System.out.println("User has purchased course with ID: " + purchasedCourse.getCourses().getCourseId());
    }

    @Test
    public void testBuyCourse() {
        // Set up mock course
        Courses course = new Courses();
        course.setCourseId(2); // Assign courseId to 2

        // Mock DAO response
        when(daoPurchase.getCourseById(2)).thenReturn(course);

        // Execute the method
        String result = purchaseBean.buyCourse(2, request);

        // Verify the result and dummy data
        assertEquals("Expected redirection to materi-back-end.xhtml", "materi-back-end.xhtml?faces-redirect=true", result);
        verify(daoPurchase, times(1)).savePurchasedCourse(any(PurchasedCourse.class));
        assertEquals((Integer) 2, course.getCourseId());  // Cast courseId to Integer

        // Print for verification
        System.out.println("Course purchased with ID: " + course.getCourseId());
    }

    @Test
    public void testBuyCourseNotLoggedIn() {
        // Simulate a case where the user is not logged in
        when(session.getAttribute("user")).thenReturn(null);

        // Execute the method
        String result = purchaseBean.buyCourse(2, request);

        // Verify the result
        assertEquals("Expected redirection to login.xhtml", "login.xhtml?faces-redirect=true", result);
        
        // Print for verification
        System.out.println("User not logged in, redirected to login page.");
    }
    
    @Test
    public void testHasPurchasedCourseFalse() {
        // Mock DAO response to return an empty list (no purchases)
        when(daoPurchase.getPurchasedCoursesByUser(mockUser.getId())).thenReturn(Arrays.asList());

        // Execute the method with a courseId that hasn't been purchased
        boolean result = purchaseBean.hasPurchasedCourse(3, request);

        // Verify the result
        assertFalse("Expected user to not have purchased course with ID 3", result);
        verify(daoPurchase, times(1)).getPurchasedCoursesByUser(mockUser.getId());

        // Print for verification
        System.out.println("User has not purchased course with ID: 3");
    }
}
