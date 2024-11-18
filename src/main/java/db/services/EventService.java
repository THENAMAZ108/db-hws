package db.services;

import db.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EventService {
    public List<Object[]> getIndividualEventsWithTies() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // language=HQL
            String hql = """
                         SELECT e.name, COUNT(r.medal)\s
                         FROM Event e JOIN Result r ON e.eventId = r.eventId\s
                         WHERE e.isTeamEvent = 0 AND r.medal = 'GOLD'\s
                         GROUP BY e.name HAVING COUNT(r.medal) > 1
                        \s""";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            return query.list();
        }
    }
}
