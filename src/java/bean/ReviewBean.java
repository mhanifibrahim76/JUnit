package bean;

import DAO.CommentsDAO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pojo.Comments;
import pojo.Users;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ReviewBean {

    private String comment;
    private int rating;
    private String username;
    private int userId;
    private List<Comments> reviews;
    private List<Comments> allComments;
    private CommentsDAO commentsDAO;
    private int commentId;

    private LoginBean sessionController;

    public ReviewBean() {
        commentsDAO = new CommentsDAO();
    }

    // Getter dan Setter
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public List<Comments> getReviews() {
        Users loggedInUser = sessionController.getLoggedInUser();
        if (loggedInUser != null) {
            return commentsDAO.getCommentsByUserId(loggedInUser.getId());
        } else {
            return null;
        }
    }

    public List<Comments> getAllComments() {
        return allComments;
    }

    public LoginBean getSessionController() {
        return sessionController;
    }

    public void setSessionController(LoginBean sessionController) {
        this.sessionController = sessionController;
    }

    // Method untuk memuat review
    public void loadReviews(HttpServletRequest request) {
        Users loggedInUser = sessionController.getLoggedInUser(); // Gunakan sessionController untuk mengambil loggedInUser
        if (loggedInUser != null) {
            reviews = commentsDAO.getCommentsByUserId(loggedInUser.getId());
        } else {
            reviews = null;
        }
        loadAllComments(); // Masih memuat semua komentar
    }

    // Method untuk submit review
    public void submitReview(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new IllegalStateException("User is not logged in!");
        }

        // Buat objek komentar dan simpan
        Comments newComment = new Comments();
        newComment.setComment(comment);
        newComment.setRating(rating);
        newComment.setUsername(username);
        newComment.setCreatedAt(new Date());

        // Set user yang terkait dengan review
        Users user = new Users();
        user.setId(userId);
        newComment.setUsers(user);

        // Simpan review
        commentsDAO.saveComment(newComment);

        // Reset form setelah submit
        comment = "";
        rating = 0;

        // Muat ulang review
        loadReviews(request);
    }

    // Method untuk menambah komentar
    public void addComment(HttpServletRequest request, HttpServletResponse response) {
        Users user = sessionController.getLoggedInUser();

        // Jika user belum login, redirect ke halaman login dan tampilkan pesan
        if (user == null) {
            try {
                request.setAttribute("message", "You need to log in to add a comment.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("login.xhtml?faces-redirect=true");
                dispatcher.forward(request, response); // Gunakan objek response yang valid
            } catch (IOException | ServletException e) {
                e.printStackTrace(); // Menangani IOException dan ServletException
            }
            return;
        }

        // Validasi komentar dan rating
        if (comment == null || comment.isEmpty()) {
            request.setAttribute("error", "Comment cannot be empty!");
            return;
        }

        if (rating < 1 || rating > 5) {
            request.setAttribute("error", "Rating must be between 1 and 5!");
            return;
        }

        // Buat objek komentar baru
        Comments newComment = new Comments();
        newComment.setComment(comment);
        newComment.setRating(rating);
        newComment.setUsername(sessionController.getUsername());
        newComment.setCreatedAt(new Date());

        // Hubungkan komentar dengan user yang login
        newComment.setUsers(user);

        // Simpan komentar ke database
        commentsDAO.saveComment(newComment);

        // Reset form setelah submit
        comment = "";
        rating = 0;

        // Muat ulang komentar setelah menambah
        loadReviews(request);
    }

    // Method untuk memuat semua komentar
    public void loadAllComments() {
        allComments = commentsDAO.getAllComments();
    }

    // Method untuk mengedit komentar
    public void editComment(Comments comment) {
        this.commentId = comment.getCommentId(); // Set commentId dari komentar yang sedang diedit
        this.comment = comment.getComment();     // Set nilai komentar untuk ditampilkan di form
        this.rating = comment.getRating();       // Set nilai rating untuk ditampilkan di form
    }

    // Method untuk mengupdate komentar
    public void updateComment(HttpServletRequest request) {
        Users user = sessionController.getLoggedInUser();

        if (user == null) {
            throw new IllegalStateException("User is not logged in!");
        }

        // Ambil komentar yang sedang diedit menggunakan commentId
        Comments updatedComment = commentsDAO.getCommentById(this.commentId);

        if (updatedComment != null && updatedComment.getUsers().getId() == user.getId()) {
            updatedComment.setComment(this.comment);
            updatedComment.setRating(this.rating);
            updatedComment.setCreatedAt(new Date());

            commentsDAO.updateComment(updatedComment);

            this.comment = "";
            this.rating = 0;
            this.commentId = 0;

            request.setAttribute("message", "Comment updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update comment!");
        }

        // Muat ulang komentar
        loadReviews(request);
    }

    // Method untuk menghapus komentar
    public void deleteComment(Comments comment, HttpServletRequest request) {
        commentsDAO.deleteComment(comment);
        // Muat ulang komentar setelah penghapusan
        reviews = commentsDAO.getCommentsByUserId(userId);
    }

    // Method untuk memeriksa apakah user telah berkomentar
    public boolean hasUserCommented(int userId) {
        for (Comments comment : allComments) {
            if (comment.getUsers().getId() == userId) {
                return true;
            }
        }
        return false;
    }
}
