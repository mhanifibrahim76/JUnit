package DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.StudyRoomUtil;
import pojo.Users;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DAOUserTest {

    private DAOUser daoUser;
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

        StudyRoomUtil.setSessionFactory(sessionFactory);

        daoUser = new DAOUser();
    }

    @After
    public void tearDown() {
        StudyRoomUtil.setSessionFactory(null);
    }

    @Test
    public void testSaveUser() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        daoUser.saveUser(user);

        verify(session).save(user);
        verify(transaction).commit();
        verify(session).close();

        assertNotNull("User should be saved", user);
        assertEquals("User saved with correct username", "testUser", user.getUsername());
        assertEquals("User saved with correct email", "test@example.com", user.getEmail());
    }

    @Test
    public void testSaveUser_Exception() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(session.beginTransaction()).thenReturn(null);

        daoUser.saveUser(user);

        verify(session).close();
//
//        assertNull("Transaction should be null to trigger exception", transaction);
    }

    @Test
    public void testIsEmailRegistered_True() {
        String email = "test@example.com";
        Users user = new Users();
        user.setEmail(email);

        Query query = mock(Query.class);
        when(session.createQuery("from Users where email= :email")).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(user);

        boolean result = daoUser.isEmailRegistered(email);

        assertTrue("Email should be registered", result);
        verify(session).close();
    }

    @Test
    public void testIsEmailRegistered_False() {
        String email = "test@example.com";

        Query query = mock(Query.class);
        when(session.createQuery("from Users where email= :email")).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        boolean result = daoUser.isEmailRegistered(email);

        assertFalse("Email should not be registered", result);
        verify(session).close();
    }

    @Test
    public void testIsEmailRegistered_Exception() {
        String email = "test@example.com";

        when(session.createQuery("from Users where email= :email"))
                .thenThrow(new RuntimeException("Database error"));

        boolean result = daoUser.isEmailRegistered(email);

        assertFalse("Exception should result in false", result);
        verify(session).close();
    }

    @Test
    public void testFindByUsername_Success() {
        String username = "testUser";
        Users expectedUser = new Users();
        expectedUser.setUsername(username);

        Query query = mock(Query.class);
        when(session.createQuery("FROM Users WHERE username = :username")).thenReturn(query);
        when(query.setParameter("username", username)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(expectedUser);

        Users result = daoUser.findByUsername(username);

        assertEquals("User should be found", expectedUser, result);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testFindByUsername_NotFound() {
        String username = "testUser";

        Query query = mock(Query.class);
        when(session.createQuery("FROM Users WHERE username = :username")).thenReturn(query);
        when(query.setParameter("username", username)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        Users result = daoUser.findByUsername(username);

        assertNull("User should not be found", result);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testFindByUsername_Exception() {
        String username = "testUser";

        when(session.createQuery("FROM Users WHERE username = :username"))
                .thenThrow(new RuntimeException("Database error"));

        Users result = daoUser.findByUsername(username);

        assertNull("Exception should result in null", result);
        verify(session).close();
    }

    @Test
    public void testIsEmailExists_True() {
        String email = "existing@example.com";

        Query query = mock(Query.class);
        when(session.createQuery("from Users where email = :email")).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new Users());

        boolean result = daoUser.isEmailExists(email);

        assertTrue("Email should exist", result);
        verify(session).close();
    }

    @Test
    public void testIsEmailExists_False() {
        String email = "notregistered@example.com";

        Query query = mock(Query.class);
        when(session.createQuery("from Users where email = :email")).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        boolean result = daoUser.isEmailExists(email);

        assertFalse("Email should not exist", result);
        verify(session).close();
    }

    @Test
    public void testIsEmailExists_Exception() {
        String email = "error@example.com";

        when(session.createQuery("from Users where email = :email"))
                .thenThrow(new RuntimeException("Database error"));

        boolean result = daoUser.isEmailExists(email);

        assertFalse("Exception should result in false", result);
        verify(session).close();
    }
}
