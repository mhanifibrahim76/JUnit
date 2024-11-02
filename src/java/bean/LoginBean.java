package bean;

import DAO.DAOLogin;
import DAO.DAOUser;
import pojo.Users;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private Users loggedInUser;
    private DAOUser daouser = new DAOUser();  // Tetap private
    private DAOLogin daologin = new DAOLogin();  // Tambahkan DAOLogin

    public LoginBean() {
    }

    // Getters and Setters for DAOUser and DAOLogin
    public void setDAOUser(DAOUser daouser) {
        this.daouser = daouser;
    }

    public void setDAOLogin(DAOLogin daologin) {
        this.daologin = daologin;
    }

    // Getter untuk testing jika diperlukan
    public DAOUser getDAOUser() {
        return this.daouser;
    }

    public DAOLogin getDAOLogin() {
        return this.daologin;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Users getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(Users loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // Method for validating login
    public String validasiLogin(HttpServletRequest request) {
        Users user = daouser.findByUsername(username);
        List<Users> us = daologin.getBy(username, password);

        if (us != null && !us.isEmpty()) {
            loggedInUser = us.get(0);
            HttpSession session = request.getSession();  // Mendapatkan session
            session.setAttribute("user", loggedInUser);  // Set user ke dalam session

            return "index?faces-redirect=true";  // Redirect ke halaman utama
        } else {
            // Handle error messages if needed
            return null;  // Tetap di halaman login jika gagal
        }
    }

    // Method for registering a new user
    public String register() {
        if (!password.equals(confirmPassword)) {
            // Handle error jika password tidak cocok
            return null;  // Tetap di halaman register
        }

        if (daouser.isEmailExists(email)) {
            // Handle jika email sudah ada
            return null;
        }

        Users newUser = new Users(username, email, password, new Date());
        daouser.saveUser(newUser);

        return "login?faces-redirect=true";  // Redirect ke halaman login setelah register berhasil
    }

    // Method for logging out
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // Ambil session jika ada
        if (session != null) {
            session.invalidate();  // Invalidasi session
        }

        return "login?faces-redirect=true";  // Redirect ke halaman login setelah logout
    }
}
