package DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import pojo.Comments;
import pojo.StudyRoomUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class CommentsDAOTest {

    private CommentsDAO commentsDAO;
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

        StudyRoomUtil.setSessionFactory(sessionFactory);  // Set the mock sessionFactory

        commentsDAO = new CommentsDAO();
    }

    @After
    public void tearDown() {
        StudyRoomUtil.setSessionFactory(null);
    }

    @Test
    public void testSaveComment() {
        // Buat objek komentar dengan data dummy
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Original Comment");  // Komentar asli
        comment.setRating(3);

        // Panggil metode saveComment pada DAO
        commentsDAO.saveComment(comment);

        // Verifikasi bahwa metode save dan commit dipanggil
        verify(session).save(comment);
        verify(transaction).commit();
        verify(session).close();

        // Assert untuk memastikan data yang disimpan sesuai
        assertEquals(Integer.valueOf(1), Integer.valueOf(comment.getCommentId())); // Menggunakan Integer.valueOf()
        assertEquals("Original Comment", comment.getComment());
        assertEquals(Integer.valueOf(3), Integer.valueOf(comment.getRating())); // Menggunakan Integer.valueOf()

        // Print untuk menunjukkan data komentar yang disimpan
        System.out.println("Comment saved: ID = " + comment.getCommentId()
                + ", Text = " + comment.getComment()
                + ", Rating = " + comment.getRating());
    }

    @Test
    public void testSaveComment_Exception() {
        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Original Comment");  // Komentar asli
        comment.setRating(3);  // Rating awal

        // Simulate exception during session.save
        doThrow(new RuntimeException("Simulated Exception")).when(session).save(comment);

        try {
            // Attempt to save comment, expecting an exception
            commentsDAO.saveComment(comment);
            fail("Expected a RuntimeException to be thrown"); // Fail if no exception is thrown
        } catch (RuntimeException e) {
            // Optionally assert the exception message
            assertEquals("Simulated Exception", e.getMessage());
        }

        // Assert that the comment is still null since save was unsuccessful
        assertNull("Comment should not be saved due to exception", comment); // Assuming you want to check if comment itself is null

        // Verify that rollback was called
        verify(transaction).rollback();
        // Verify that session was closed
        verify(session).close();

        // Print message for confirmation
        System.out.println("Exception occurred while saving Comment. The comment remains null.");
    }

    @Test
    public void testGetCommentsByUserId_Success() {
        int userId = 1;

        // Membuat data dummy untuk komentar
        Comments dummyComment = new Comments();
        dummyComment.setCommentId(1);
        dummyComment.setComment("Test Comment for User");
        dummyComment.setRating(4);

        List<Comments> expectedComments = new ArrayList<>();
        expectedComments.add(dummyComment);

        // Mock Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);
        when(session.createQuery("FROM Comments WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.list()).thenReturn(expectedComments);

        // Memanggil metode yang akan diuji
        List<Comments> result = commentsDAO.getCommentsByUserId(userId);

        // Verifikasi hasil
        assertEquals(expectedComments, result);
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(1), Integer.valueOf(result.get(0).getCommentId()));
        assertEquals("Test Comment for User", result.get(0).getComment());
        assertEquals(Integer.valueOf(4), Integer.valueOf(result.get(0).getRating()));

        // Pastikan session ditutup
        verify(session).close();

        // Print pesan hasil
        System.out.println("Retrieved Comment ID: " + result.get(0).getCommentId());
        System.out.println("Retrieved Comment: " + result.get(0).getComment());
        System.out.println("Retrieved Rating: " + result.get(0).getRating());
    }

    @Test
    public void testGetCommentsByUserId_Exception() {
        int userId = 1;

        // Membuat data dummy untuk komentar
        Comments dummyComment = new Comments();
        dummyComment.setCommentId(1);
        dummyComment.setComment("Test Comment for User");
        dummyComment.setRating(4);

        List<Comments> expectedComments = new ArrayList<>();
        expectedComments.add(dummyComment);

        // Mock Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);
        when(session.createQuery("FROM Comment WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.list()).thenReturn(expectedComments);
        List<Comments> result = commentsDAO.getCommentsByUserId(userId);

        assertNull(result);  // Result harus null jika terjadi exception
        verify(session).close();  // Pastikan session ditutup
    }

    @Test
    public void testGetAllComments() {
        List<Comments> expectedComments = new ArrayList<>();
        expectedComments.add(new Comments());

        // Mock the Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);
        when(session.createQuery("FROM Comments")).thenReturn(query);
        when(query.list()).thenReturn(expectedComments);

        List<Comments> result = commentsDAO.getAllComments();

        assertEquals(expectedComments, result);
        verify(session).close();
    }

    @Test
    public void testGetAllComments_Exception() {
        List<Comments> expectedComments = new ArrayList<>();
        expectedComments.add(new Comments());

        // Mock the Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);
        when(session.createQuery("FROM Comments")).thenReturn(null);
        when(query.list()).thenReturn(expectedComments);

        List<Comments> result = commentsDAO.getAllComments();

        // Verifikasi hasil
        assertNull(result);  // Hasil harus null jika terjadi exception
        verify(session).close();  // Pastikan sesi ditutup
    }

    @Test
    public void testUpdateComment() {
        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Original Comment");  // Komentar asli
        comment.setRating(3);  // Rating awal

        // Simulate updating the comment
        comment.setComment("Updated Comment");  // Komentar diubah
        comment.setRating(5);  // Rating diubah

        // Call updateComment
        commentsDAO.updateComment(comment);

        // Verifikasi bahwa update dipanggil dengan data yang benar
        verify(session).update(comment);
        verify(transaction).commit();
        verify(session).close();

        assertEquals("Updated Comment", comment.getComment()); // Memeriksa komentar yang diperbarui
        assertEquals(5, (int) comment.getRating()); // Memeriksa rating yang diperbarui, casting ke int
        assertEquals(1, (int) comment.getCommentId()); // Memeriksa ID komentar, casting ke int

        // Print the updated comment
        System.out.println("Updated Comment ID: " + comment.getCommentId());
        System.out.println("Updated Comment: " + comment.getComment());
        System.out.println("Updated Rating: " + comment.getRating());
    }

    @Test
    public void testUpdateComment_Exception() {
        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Original Comment");  // Komentar asli
        comment.setRating(3);  // Rating awal

        // Simulate the values before update
        String originalComment = comment.getComment(); // Simpan nilai asli
        int originalRating = comment.getRating(); // Simpan rating asli

        // Simulate exception during session.update
        doThrow(new RuntimeException("Simulated Exception")).when(session).update(comment);

        // Call updateComment and expect an exception
        try {
            // Call the method to update the comment, but we should not modify comment before this call
            commentsDAO.updateComment(comment);
        } catch (RuntimeException e) {
            // Ignore expected exception
        }

        // Assert that the comment properties remain unchanged
        assertEquals(originalComment, comment.getComment()); // Memeriksa komentar yang tidak berubah
        assertEquals(originalRating, (int) comment.getRating()); // Memeriksa rating yang tidak berubah
        assertEquals(1, (int) comment.getCommentId()); // Memeriksa ID komentar yang tidak berubah

        // Verify that rollback was called
        verify(transaction).rollback();
        verify(session).close();

        // Print the original comment (seharusnya tidak berubah)
        System.out.println("Comment ID: " + comment.getCommentId());
        System.out.println("Comment: " + comment.getComment());
        System.out.println("Rating: " + comment.getRating());
    }

    @Test
    public void testDeleteComment() {
        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Comment to be deleted");  // Komentar yang akan dihapus
        comment.setRating(3);  // Rating komentar

        // Panggil deleteComment
        commentsDAO.deleteComment(comment);

        // Verifikasi bahwa metode delete dipanggil dengan data yang benar
        verify(session).delete(comment);
        verify(transaction).commit();
        verify(session).close();

        // Assertions untuk memeriksa bahwa data yang dihapus benar
        assertEquals(1, (int) comment.getCommentId()); // Memeriksa ID komentar
        assertEquals("Comment to be deleted", comment.getComment()); // Memeriksa komentar yang dihapus
        assertEquals(3, (int) comment.getRating()); // Memeriksa rating komentar

        // Tambahkan verifikasi bahwa komentar sudah dihapus
        when(session.get(Comments.class, 1)).thenReturn(null); // Simulasi bahwa tidak ada komentar dengan ID 1
        Comments deletedComment = commentsDAO.getCommentById(1); // Memanggil metode untuk mendapatkan komentar berdasarkan ID
        assertNull(deletedComment); // Memastikan komentar sudah tidak ada

        // Print pesan bahwa komentar telah dihapus
        System.out.println("Deleted Comment ID: " + comment.getCommentId());
        System.out.println("Deleted Comment: " + comment.getComment());
    }

    @Test
    public void testDeleteComment_Exception() {
        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("Comment to be deleted");  // Komentar yang akan dihapus
        comment.setRating(3);  // Rating komentar

        // Simulate exception during session.delete
        doThrow(new RuntimeException("Simulated Exception")).when(session).delete(comment);

        // Panggil deleteComment
        commentsDAO.deleteComment(comment);

        // Verifikasi bahwa rollback terjadi
        verify(transaction).rollback();
        verify(session).close();

        // Assertion untuk memastikan bahwa komentar tetap ada
        when(session.get(Comments.class, 1)).thenReturn(comment); // Simulasi bahwa komentar masih ada
        Comments existingComment = commentsDAO.getCommentById(1); // Memanggil metode untuk mendapatkan komentar berdasarkan ID
        assertNotNull(existingComment); // Memastikan komentar masih ada setelah exception
        assertEquals(1, (int) existingComment.getCommentId()); // Memastikan ID komentar
        assertEquals("Comment to be deleted", existingComment.getComment()); // Memastikan isi komentar
        assertEquals(3, (int) existingComment.getRating()); // Memastikan rating komentar

        // Print pesan bahwa terjadi exception saat menghapus
        System.out.println("Failed to delete Comment ID: " + comment.getCommentId());
        System.out.println("Comment still exists: " + existingComment.getComment());
    }

    @Test
    public void testGetCommentById() {
        int commentId = 1;

        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(commentId);
        comment.setComment("This is a test comment.");  // Dummy comment text
        comment.setRating(4);  // Dummy rating
        comment.setCreatedAt(new Date());  // Set created date to now

        // Simulate session.get behavior
        when(session.get(Comments.class, commentId)).thenReturn(comment);

        // Call the method to test
        Comments result = commentsDAO.getCommentById(commentId);

        // Assertions
        assertEquals(comment, result);

        // Print the retrieved comment as proof
        System.out.println("Retrieved Comment ID: " + result.getCommentId());
        System.out.println("Retrieved Comment: " + result.getComment());
        System.out.println("Retrieved Rating: " + result.getRating());
        System.out.println("Retrieved Created At: " + result.getCreatedAt());

        // Verify that the session is closed
        verify(session).close();
    }

    @Test
    public void testGetCommentById_Exception() {
        int commentId = 1;

        // Setup dummy comment data
        Comments comment = new Comments();
        comment.setCommentId(commentId);
        comment.setComment("This is a test comment.");  // Dummy comment text
        comment.setRating(4);  // Dummy rating
        comment.setCreatedAt(new Date());  // Set created date to now

        // Simulate session.get behavior
        when(session.get(Comments.class, commentId)).thenThrow(new RuntimeException("Simulated Exception"));
        when(session.get(Comments.class, null)).thenReturn(null);

        // Call the method to test
        Comments result = commentsDAO.getCommentById(commentId);

        assertNull(result);  // Result harus null jika terjadi exception
        verify(session).close();  // Pastikan session ditutup
    }

    @Test
    public void testHasUserCommented_True() {
        int userId = 1;

        // Buat dummy comment sebagai konteks
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("User's first comment");
        comment.setRating(4);

        // Mock Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);

        // Simulasi pembuatan query dan pengaturan parameter
        when(session.createQuery("SELECT COUNT(*) FROM Comments WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);

        // Mengatur hasil dari uniqueResult() untuk mengembalikan Long dengan nilai lebih dari 0
        when(query.uniqueResult()).thenReturn(1L);  // Mengindikasikan bahwa ada komentar untuk userId ini

        // Panggil metode yang diuji
        boolean result = commentsDAO.hasUserCommented(userId);

        // Verifikasi hasilnya
        assertTrue(result);
        assertEquals("Expected query count to be 1 for user ID: " + userId, Long.valueOf(1), query.uniqueResult());

        // Pastikan sesi ditutup
        verify(session).close();

        // Print pesan hasil untuk konfirmasi
        System.out.println("User with ID " + userId + " has commented: " + result);
        System.out.println("Comment ID: " + comment.getCommentId());
        System.out.println("Comment Text: " + comment.getComment());
        System.out.println("Rating: " + comment.getRating());
    }

    @Test
    public void testHasUserCommented_False() {
        int userId = 1;

        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("User's first comment");
        comment.setRating(4);

        // Mock Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);

        // Simulasi pembuatan query dan pengaturan parameter
        when(session.createQuery("SELECT COUNT(*) FROM Comments WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);

        // Mengatur hasil dari uniqueResult() untuk mengembalikan Long dengan nilai 0, mengindikasikan tidak ada komentar
        when(query.uniqueResult()).thenReturn(0L);

        // Panggil metode yang diuji
        boolean result = commentsDAO.hasUserCommented(userId);

        // Verifikasi hasilnya
        assertFalse(result);
        assertEquals("Expected query count to be 0 for user ID: " + userId, Long.valueOf(0), query.uniqueResult());

        // Pastikan sesi ditutup
        verify(session).close();

        // Print pesan hasil untuk konfirmasi
        System.out.println("User with ID " + userId + " has commented: " + result);
    }

    @Test
    public void testHasUserCommented_Exception() {
        int userId = 1;

        // Buat dummy comment sebagai konteks
        Comments comment = new Comments();
        comment.setCommentId(1);
        comment.setComment("User's first comment");
        comment.setRating(4);

        // Mock Query object
        org.hibernate.Query query = mock(org.hibernate.Query.class);

        // Simulasi pembuatan query dan pengaturan parameter
        when(session.createQuery("SELECT COUNT(*) FROM Comment WHERE users.id = :userId")).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);

        // Mengatur hasil dari uniqueResult() untuk mengembalikan Long dengan nilai lebih dari 0
        when(query.uniqueResult()).thenReturn(1L);  // Mengindikasikan bahwa ada komentar untuk userId ini

        // Panggil metode yang diuji
        boolean result = commentsDAO.hasUserCommented(userId);

        assertFalse(result);  // Result harus false jika terjadi exception
        verify(session).close();  // Pastikan session ditutup
    }

}
