package db.services;

import db.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PlayerService {
    public List<Object[]> getPlayersWithGoldMedalsIn2004() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // language=HQL
            String hql = """
                         SELECT p.birthDate, COUNT(p.playerId), COUNT(r.medal)\s
                         FROM Player p JOIN Result r ON p.playerId = r.playerId\s
                         JOIN Event e ON r.eventId = e.eventId\s
                         JOIN Olympic o ON e.olympicId = o.olympicId\s
                         WHERE o.year = 2004 AND r.medal = 'GOLD'\s
                         GROUP BY p.birthDate
                        \s""";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            return query.list();
        }
    }

    public List<Object[]> getPlayersWithMedals() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // language=HQL
            String hql = """
                         SELECT p.name, o.olympicId\s
                         FROM Player p JOIN Result r ON p.playerId = r.playerId\s
                         JOIN Event e ON r.eventId = e.eventId\s
                         JOIN Olympic o ON e.olympicId = o.olympicId\s
                         WHERE r.medal IN ('GOLD', 'SILVER', 'BRONZE')\s
                         GROUP BY p.name, o.olympicId
                        \s""";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            return query.list();
        }
    }

    public String getCountryWithHighestPercentageOfPlayersWithVowelStart() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> totalPlayers = getTotalPlayersByCountry(session);
            List<Object[]> vowelPlayers = getVowelPlayersByCountry(session);

            String countryWithHighestPercentage = null;
            double highestPercentage = 0.0;

            for (Object[] total : totalPlayers) {
                String countryId = (String) total[0];
                long totalCount = (long) total[1];

                for (Object[] vowel : vowelPlayers) {
                    if (countryId.equals(vowel[0])) {
                        long vowelCount = (long) vowel[1];
                        double percentage = (double) vowelCount / totalCount;
                        if (percentage > highestPercentage) {
                            highestPercentage = percentage;
                            countryWithHighestPercentage = countryId;
                        }
                        break;
                    }
                }
            }

            return countryWithHighestPercentage;
        }
    }

    private List<Object[]> getTotalPlayersByCountry(Session session) {
        // language=HQL
        String totalPlayersHql = """
                     SELECT p.countryId, COUNT(p.playerId)\s
                     FROM Player p\s
                     GROUP BY p.countryId\s
                    \s""";
        Query<Object[]> totalPlayersQuery = session.createQuery(totalPlayersHql, Object[].class);
        return totalPlayersQuery.list();
    }

    private List<Object[]> getVowelPlayersByCountry(Session session) {
        // language=HQL
        String vowelPlayersHql = """
                     SELECT p.countryId, COUNT(p.playerId)\s
                     FROM Player p\s
                     WHERE LOWER(SUBSTRING(p.name, 1, 1)) IN ('a', 'e', 'i', 'o', 'u')\s
                     GROUP BY p.countryId\s
                    \s""";
        Query<Object[]> vowelPlayersQuery = session.createQuery(vowelPlayersHql, Object[].class);
        return vowelPlayersQuery.list();
    }
}



