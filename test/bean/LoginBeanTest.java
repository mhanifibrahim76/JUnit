package bean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import DAO.DAOLogin;
import DAO.DAOUser;
import pojo.Users;

import java.util.ArrayList;
import java.util.List;

public class LoginBeanTest {

    @Mock
    private DAOUser mockDaoUser;

    @Mock
    private DAOLogin mockDaoLogin;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpSession mockSession;

    @InjectMocks
    private LoginBean loginBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginBean = new LoginBean();
        loginBean.setDAOUser(mockDaoUser);
        loginBean.setDAOLogin(mockDaoLogin);
    }

    // Test validasiLogin method
    @Test
    public void testValidasiLoginSuccess() {
        // Set up test data
        loginBean.setUsername("testuser");
        loginBean.setPassword("password");

        Users testUser = new Users("testuser", "testuser@example.com", "password", null);
        List<Users> userList = new ArrayList<>();
        userList.add(testUser);

        when(mockDaoLogin.getBy("testuser", "password")).thenReturn(userList);
        when(mockRequest.getSession()).thenReturn(mockSession);

        // Call the method
        String result = loginBean.validasiLogin(mockRequest);

        // Verify interactions and result
        verify(mockSession).setAttribute("user", testUser);
        assertEquals("index?faces-redirect=true", result);

        // Additional assertions to check dummy data
        assertEquals("testuser", testUser.getUsername());
        assertEquals("testuser@example.com", testUser.getEmail());
        System.out.println("Login success: Username - " + testUser.getUsername() + ", Email - " + testUser.getEmail());
    }

    @Test
    public void testValidasiLoginFailure() {
        // Set up test data
        loginBean.setUsername("wronguser");
        loginBean.setPassword("wrongpassword");

        when(mockDaoLogin.getBy("wronguser", "wrongpassword")).thenReturn(new ArrayList<>());

        // Call the method
        String result = loginBean.validasiLogin(mockRequest);

        // Verify interactions and result
        assertNull(result);
        System.out.println("Login failed for Username: wronguser");
    }

    // Test register method
    @Test
    public void testRegisterSuccess() {
        // Set up test data
        loginBean.setUsername("newuser");
        loginBean.setEmail("newuser@example.com");
        loginBean.setPassword("password");
        loginBean.setConfirmPassword("password");

        when(mockDaoUser.isEmailExists("newuser@example.com")).thenReturn(false);

        // Call the method
        String result = loginBean.register();

        // Verify interactions and result
        verify(mockDaoUser).saveUser(any(Users.class));
        assertEquals("login?faces-redirect=true", result);

        // Additional assertion to confirm saved user data
        assertEquals("newuser", loginBean.getUsername());
        assertEquals("newuser@example.com", loginBean.getEmail());
        System.out.println("Registration success: Username - " + loginBean.getUsername() + ", Email - " + loginBean.getEmail());
    }

    @Test
    public void testRegisterPasswordMismatch() {
        // Set up test data
        loginBean.setPassword("password");
        loginBean.setConfirmPassword("differentpassword");

        // Call the method
        String result = loginBean.register();

        // Verify interactions and result
        assertNull(result);
        System.out.println("Registration failed due to password mismatch");
    }

    @Test
    public void testRegisterEmailExists() {
        // Set up test data
        loginBean.setEmail("existing@example.com");
        loginBean.setPassword("password");
        loginBean.setConfirmPassword("password");

        when(mockDaoUser.isEmailExists("existing@example.com")).thenReturn(true);

        // Call the method
        String result = loginBean.register();

        // Verify interactions and result
        assertNull(result);
        System.out.println("Registration failed: Email already exists - " + loginBean.getEmail());
    }

    // Test logout method
    @Test
    public void testLogout() {
        // Set up mock session
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        // Call the method
        String result = loginBean.logout(mockRequest);

        // Verify interactions and result
        verify(mockSession).invalidate();
        assertEquals("login?faces-redirect=true", result);
        System.out.println("Logout successful, redirected to login page");
    }
}
