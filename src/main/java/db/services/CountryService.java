package db.services;

import db.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CountryService {
    public List<Object[]> getCountriesWithLowestTeamMedalRatio(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // language=HQL
            String hql = """
                         SELECT c.name, COUNT(r.medal), c.population\s
                         FROM Country c JOIN Player p ON c.countryId = p.countryId\s
                         JOIN Result r ON p.playerId = r.playerId\s
                         JOIN Event e ON r.eventId = e.eventId\s
                         JOIN Olympic o ON e.olympicId = o.olympicId\s
                         WHERE o.year = 2000 AND e.isTeamEvent = 1\s
                         GROUP BY c.name, c.population\s
                         """;
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> results = query.list();

            return results.stream()
                    .map(row -> new Object[]{row[0], (long) row[1] * 1.0 / (long) row[2]})
                    .sorted(Comparator.comparingDouble(a -> (double) a[1]))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }
}

//lolkekcheburekwithcheese
