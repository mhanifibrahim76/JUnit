package DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.Courses;
import pojo.PurchasedCourse;
import pojo.StudyRoomUtil;
import java.util.List;

public class DAOPurchase {

    // Method to save a purchased course in the database
    public void savePurchasedCourse(PurchasedCourse purchasedCourse) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = StudyRoomUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(purchasedCourse);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // Method to find a course by its ID
    public Courses getCourseById(int courseId) {
        Transaction transaction = null;
        Courses course = null;
        Session session = null;
        try {
            session = StudyRoomUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            course = (Courses) session.get(Courses.class, courseId); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return course;
    }
    
    public List<PurchasedCourse> getPurchasedCoursesByUser(int userId) {
        Session session = StudyRoomUtil.getSessionFactory().openSession();
        List<PurchasedCourse> purchasedCourses = null;
        try {
            purchasedCourses = session.createQuery("FROM PurchasedCourse WHERE users.id = :userId")
                                      .setParameter("userId", userId)
                                      .list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return purchasedCourses;
    }
}
