/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template file in the editor.
 */
package bean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DAO.CommentsDAO;
import pojo.Comments;
import pojo.Users;

public class ReviewBeanTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpSession mockSession;

    @Mock
    private CommentsDAO mockCommentsDAO;

    @Mock
    private LoginBean mockLoginBean;

    @InjectMocks
    private ReviewBean reviewBean;

    private Users loggedInUser;
    private List<Comments> mockCommentsList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize test data
        loggedInUser = new Users();
        loggedInUser.setId(1);  // Set a user ID for testing

        mockCommentsList = new ArrayList<>();

        // Create a comment and set its fields
        Comments comment = new Comments();
        comment.setComment("Great service!");
        comment.setUsers(loggedInUser);
        comment.setCreatedAt(new Date());
        comment.setRating(5); // Set a rating for the comment

        mockCommentsList.add(comment);

        reviewBean.setSessionController(mockLoginBean);
    }

    @Test
    public void testLoadReviews_WithLoggedInUser() {
        when(mockLoginBean.getLoggedInUser()).thenReturn(loggedInUser);
        when(mockCommentsDAO.getCommentsByUserId(loggedInUser.getId())).thenReturn(mockCommentsList);

        reviewBean.loadReviews(mockRequest);

        verify(mockCommentsDAO).getCommentsByUserId(loggedInUser.getId());

        assertEquals("Expected one comment in the reviews list", 1, reviewBean.getReviews().size());
        assertEquals("Comment content does not match", "Great service!", reviewBean.getReviews().get(0).getComment());
        assertEquals("Rating should be 5", Integer.valueOf(5), Integer.valueOf(reviewBean.getReviews().get(0).getRating()));

        // Print output for verification
        System.out.println("Loaded reviews for user: " + loggedInUser.getId());
    }

    @Test
    public void testLoadReviews_NoLoggedInUser() {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(null);

        reviewBean.loadReviews(mockRequest);

        verify(mockCommentsDAO, never()).getCommentsByUserId(anyInt());

        assertNull("Expected reviews list to be null when no user is logged in", reviewBean.getReviews());

        // Print output for verification
        System.out.println("No logged-in user found. Reviews list is null.");
    }

    @Test
    public void testLoadReviews_NoSession() {
        when(mockRequest.getSession(false)).thenReturn(null);

        reviewBean.loadReviews(mockRequest);

        verify(mockCommentsDAO, never()).getCommentsByUserId(anyInt());

        assertNull("Expected reviews list to be null when there is no session", reviewBean.getReviews());

        // Print output for verification
        System.out.println("No session found. Reviews list is null.");
    }

    @Test
    public void testSubmitReview_UserLoggedIn() {
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn(loggedInUser.getId());

        reviewBean.setComment("Excellent product!");
        reviewBean.setRating(4);

        reviewBean.submitReview(mockRequest);

        verify(mockCommentsDAO, times(1)).saveComment(any(Comments.class));

        assertEquals("Comment should be reset to empty after submit", "", reviewBean.getComment());
        assertEquals("Rating should be reset to 0 after submit", 0, reviewBean.getRating());

        // Print output for verification
        System.out.println("Review submitted successfully for user: " + loggedInUser.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testSubmitReview_UserNotLoggedIn() {
        when(mockRequest.getSession(true)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn(null);

        reviewBean.submitReview(mockRequest);

        // Print output for verification
        System.out.println("Attempted to submit review without a logged-in user.");
    }

    @Test
    public void testAddComment_UserNotLoggedIn() throws ServletException, IOException {
        when(mockLoginBean.getLoggedInUser()).thenReturn(null);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(mockRequest.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        reviewBean.addComment(mockRequest, mockResponse);

        verify(mockRequest).setAttribute(eq("message"), eq("You need to log in to add a comment."));
        verify(dispatcher).forward(mockRequest, mockResponse);

        // Print output for verification
        System.out.println("User not logged in; redirected to login page.");
    }

    @Test
    public void testAddComment_UserLoggedIn_ValidComment() throws ServletException, IOException {
        when(mockLoginBean.getLoggedInUser()).thenReturn(loggedInUser);

        reviewBean.setComment("Amazing experience!");
        reviewBean.setRating(5);

        reviewBean.addComment(mockRequest, mockResponse);

        verify(mockCommentsDAO, times(1)).saveComment(any(Comments.class));

        assertEquals("Comment should be reset to empty after adding", "", reviewBean.getComment());
        assertEquals("Rating should be reset to 0 after adding", 0, reviewBean.getRating());

        // Print output for verification
        System.out.println("Comment added successfully for user: " + loggedInUser.getId());
    }

    @Test
    public void testAddComment_EmptyComment() throws ServletException, IOException {
        when(mockLoginBean.getLoggedInUser()).thenReturn(loggedInUser);
        reviewBean.setComment("");  // Simulasi komentar kosong
        reviewBean.setRating(3);    // Set rating valid untuk fokus pada komentar kosong

        reviewBean.addComment(mockRequest, mockResponse);

        verify(mockRequest).setAttribute("error", "Comment cannot be empty!");
        verify(mockCommentsDAO, never()).saveComment(any(Comments.class));
        System.out.println("Error message for empty comment triggered as expected.");
    }

    @Test
    public void testAddComment_InvalidRating() throws ServletException, IOException {
        when(mockLoginBean.getLoggedInUser()).thenReturn(loggedInUser);
        reviewBean.setComment("Good service!");
        reviewBean.setRating(6);  // Simulasi rating di luar batas (1-5)

        reviewBean.addComment(mockRequest, mockResponse);

        verify(mockRequest).setAttribute("error", "Rating must be between 1 and 5!");
        verify(mockCommentsDAO, never()).saveComment(any(Comments.class));
        System.out.println("Error message for invalid rating triggered as expected.");
    }

    @Test
    public void testAddComment_ExceptionHandling() throws ServletException, IOException {
        when(mockLoginBean.getLoggedInUser()).thenReturn(null);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(mockRequest.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        doThrow(new IOException()).when(dispatcher).forward(mockRequest, mockResponse);

        reviewBean.addComment(mockRequest, mockResponse);

        verify(mockRequest).setAttribute("message", "You need to log in to add a comment.");
        System.out.println("IOException caught and handled as expected.");
    }

    @Test
    public void testEditComment() {
        // Buat instance komentar untuk pengujian
        Comments comment = new Comments();
        comment.setCommentId(1); // Atur id komentar
        comment.setComment("Sample comment for editing"); // Atur isi komentar
        comment.setRating(4); // Atur rating komentar

        // Panggil metode editComment
        reviewBean.editComment(comment);

        // Verifikasi bahwa nilai-nilai dari objek comment berhasil diatur pada ReviewBean
        assertEquals("Comment ID should match the edited comment's ID", 1, reviewBean.getCommentId());
        assertEquals("Comment content should match the edited comment's content", "Sample comment for editing", reviewBean.getComment());
        assertEquals("Rating should match the edited comment's rating", 4, reviewBean.getRating());

        // Print output for verification
        System.out.println("EditComment method executed successfully. Comment ID: " + reviewBean.getCommentId() + ", Content: " + reviewBean.getComment() + ", Rating: " + reviewBean.getRating());
    }

    @Test
    public void testUpdateComment_UserLoggedIn() {
        when(mockLoginBean.getLoggedInUser()).thenReturn(loggedInUser);
        Comments existingComment = new Comments();
        existingComment.setCommentId(1);
        existingComment.setComment("Before Udated comment content");
        existingComment.setRating(1);
        existingComment.setUsers(loggedInUser);
        when(mockCommentsDAO.getCommentById(1)).thenReturn(existingComment);

        reviewBean.setCommentId(1);
        reviewBean.setComment("Updated comment content");
        reviewBean.setRating(4);

        reviewBean.updateComment(mockRequest);

        verify(mockCommentsDAO, times(1)).updateComment(existingComment);

        assertEquals("Expected updated comment content", "Updated comment content", existingComment.getComment());
        assertEquals("Expected updated rating", Integer.valueOf(4), Integer.valueOf(existingComment.getRating()));

        // Print output for verification
        System.out.println("Comment updated successfully for comment ID: " + existingComment.getCommentId());
        System.out.println("Comment updated successfully for comment ID: " + existingComment.getComment());
        System.out.println("Comment updated successfully for comment ID: " + existingComment.getRating());
        System.out.println("=============================================================");
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateComment_UserNotLoggedIn() {
        // Set up scenario where logged-in user is null
        when(mockLoginBean.getLoggedInUser()).thenReturn(null);

        // Call the method, which should throw IllegalStateException
        reviewBean.updateComment(mockRequest);

        // Verification print for output
        System.out.println("Attempted to update comment without a logged-in user.");
    }

    @Test
    public void testDeleteComment() {
        Comments commentToDelete = new Comments();
        commentToDelete.setCommentId(1);

        reviewBean.deleteComment(commentToDelete, mockRequest);

        verify(mockCommentsDAO, times(1)).deleteComment(commentToDelete);

        // Print output for verification
        System.out.println("Comment with ID " + commentToDelete.getCommentId() + " was successfully deleted.");
    }

    @Test
    public void testHasUserCommented_UserHasCommented() {
        // Siapkan data komentar yang memiliki user ID yang sesuai
        Comments comment = new Comments();
        
        comment.setUsers(loggedInUser);  // Menggunakan user ID dari loggedInUser
        mockCommentsList.add(comment);

        // Simulasikan metode getComments yang mengembalikan daftar komentar
        when(mockCommentsDAO.getAllComments()).thenReturn(mockCommentsList);
        reviewBean.loadReviews(mockRequest);

        boolean result = reviewBean.hasUserCommented(loggedInUser.getId());

        assertTrue("Expected result to be true when user has commented", result);
    }

    @Test
    public void testHasUserCommented_UserHasNotCommented() {
        // Daftar komentar kosong atau tanpa user ID yang sesuai
        when(mockCommentsDAO.getAllComments()).thenReturn(new ArrayList<>()); // atau gunakan mockCommentsList jika kosong
        reviewBean.loadReviews(mockRequest);

        boolean result = reviewBean.hasUserCommented(loggedInUser.getId());

        assertFalse("Expected result to be false when user has not commented", result);
    }

}
