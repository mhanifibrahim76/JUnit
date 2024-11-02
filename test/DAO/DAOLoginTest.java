package DAO;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import pojo.StudyRoomUtil;
import pojo.Users;

public class DAOLoginTest {

    private DAOLogin daoLogin;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    public DAOLoginTest() {
    }

    @Before
    public void setUp() {
        sessionFactory = mock(SessionFactory.class);
        session = mock(Session.class);
        transaction = mock(Transaction.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        StudyRoomUtil.setSessionFactory(sessionFactory);  // Set the mock sessionFactory

        daoLogin = new DAOLogin();
    }

    @After
    public void tearDown() {
        StudyRoomUtil.setSessionFactory(null);
    }

    /**
     * Test of getBy method, of class DAOLogin.
     */
    @Test
    public void testGetBy_Success() {
        System.out.println("getBy_Success");
        String uName = "testUser";
        String uPass = "testPass";

        // Mengisi expResult dengan objek Users yang memiliki data
        Users user = new Users();
        user.setUsername(uName);
        user.setPassword(uPass);
        List<Users> expResult = new ArrayList<>();
        expResult.add(user);

        Query query = mock(Query.class);
        when(session.createQuery("from Users where username= :uName AND password= :uPass")).thenReturn(query);
        when(query.setParameter("uName", uName)).thenReturn(query);
        when(query.setParameter("uPass", uPass)).thenReturn(query);
        when(query.list()).thenReturn(expResult);

        List<Users> result = daoLogin.getBy(uName, uPass);

        // Verifikasi hasil
        assertEquals(expResult, result);

        // Print untuk bukti data yang diambil
        System.out.println("Hasil query:");
        result.forEach(u -> System.out.println("Username: " + u.getUsername() + ", Password: " + u.getPassword()));

        // Verifikasi transaksi dan session
        verify(session).createQuery(eq("from Users where username= :uName AND password= :uPass"));
        verify(query).setParameter("uName", uName);
        verify(query).setParameter("uPass", uPass);
        verify(query).list();
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testGetBy_Failure() {
        System.out.println("getBy_Failure");
        String uName = "testUser";
        String uPass = "testPass";

        // Mengisi expResult dengan objek Users yang memiliki data
        Users user = new Users();
        user.setUsername(uName);
        user.setPassword(uPass);
        List<Users> expResult = new ArrayList<>();
        expResult.add(user);

        Query query = mock(Query.class);
        when(session.createQuery("from Users where username= :uName AND password= :uPass")).thenReturn(null);
        when(query.setParameter("uName", uName)).thenReturn(query);
        when(query.setParameter("uPass", uPass)).thenReturn(query);
        when(query.list()).thenReturn(expResult);

        List<Users> result = daoLogin.getBy(uName, uPass);

        assertTrue(result.isEmpty());
        verify(transaction).rollback();
        verify(session).close();
    }

   
}
