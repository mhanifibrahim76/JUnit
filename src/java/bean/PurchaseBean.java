package bean;

import DAO.DAOPurchase;
import pojo.Courses;
import pojo.PurchasedCourse;
import pojo.Users;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "purchaseBean")
@SessionScoped
public class PurchaseBean {

    private DAOPurchase DAOPurchase = new DAOPurchase();
    private String selectedCourseId;

    // Setter for DAOPurchase (added for testing)
    public void setDAOPurchase(DAOPurchase DAOPurchase) {
        this.DAOPurchase = DAOPurchase;
    }

    // Getter and Setter for selectedCourseId
    public String getSelectedCourseId() {
        return selectedCourseId;
    }

    public void setSelectedCourseId(String selectedCourseId) {
        this.selectedCourseId = selectedCourseId;
    }

    public boolean hasPurchasedCourse(int courseId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Users loggedUser = (Users) session.getAttribute("user");

        if (loggedUser == null) {
            return false;  // User is not logged in
        }

        List<PurchasedCourse> purchasedCourses = DAOPurchase.getPurchasedCoursesByUser(loggedUser.getId());

        for (PurchasedCourse purchasedCourse : purchasedCourses) {
            if (purchasedCourse.getCourses().getCourseId() == courseId) {
                return true;
            }
        }

        return false;
    }

    public String buyCourse(int courseId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Users loggedUser = (Users) session.getAttribute("user");

        if (loggedUser == null) {
            return "login.xhtml?faces-redirect=true";
        }

        Courses course = DAOPurchase.getCourseById(courseId);

        PurchasedCourse purchasedCourse = new PurchasedCourse();
        purchasedCourse.setCourses(course);
        purchasedCourse.setUsers(loggedUser);
        purchasedCourse.setPurchaseDate(new Date());

        DAOPurchase.savePurchasedCourse(purchasedCourse);

        switch (courseId) {
            case 1:
                return "materi-front-end.xhtml?faces-redirect=true";
            case 2:
                return "materi-back-end.xhtml?faces-redirect=true";
            case 3:
                return "materi-frame-work.xhtml?faces-redirect=true";
            default:
                return "courses.xhtml";
        }
    }
}
