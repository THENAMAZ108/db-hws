package db.repositories;

import db.models.Olympic;
import db.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class OlympicRepository {
    public void saveOlympic(Olympic olympic) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(olympic);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Olympic> getAllOlympics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Olympic> query = session.createQuery("from Olympic", Olympic.class);
            return query.list();
        }
    }
}
