/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

/**
 *
 * @author mhanifibrahim7890
 */


import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.StudyRoomUtil;
import pojo.Users;

public class DAOLogin {
    public List<Users> getBy(String uName, String uPass) {
    List<Users> user = new ArrayList();
    Transaction trans = null;
    Session session = StudyRoomUtil.getSessionFactory().openSession();
    try {
        trans = session.beginTransaction();
        Query query = session.createQuery("from Users where username= :uName AND password= :uPass");
        query.setParameter("uName", uName);
        query.setParameter("uPass", uPass);
        user = query.list();
        trans.commit();
    } catch (Exception e) {
        if (trans != null) {
            trans.rollback();
        }
        System.out.println("[ERROR] DAOLogin.getBy: " + e.getMessage());
        e.printStackTrace();
    } finally {
        session.close(); // Always close session
    }
    return user;
}

}
