package db.utils;

import com.github.javafaker.Faker;
import db.models.*;
import db.repositories.*;

import java.util.List;

public class DatabaseSeeder {
    public static void seedData(int count) {
        Faker faker = new Faker();
        CountryRepository countryRepository = new CountryRepository();
        OlympicRepository olympicRepository = new OlympicRepository();
        PlayerRepository playerRepository = new PlayerRepository();
        EventRepository eventRepository = new EventRepository();
        ResultRepository resultRepository = new ResultRepository();

        // Генерация стран
        for (int i = 0; i < count; i++) {
            Country country = new Country();
            country.setCountryId(faker.regexify("[A-Z]{3}"));
            country.setName(faker.address().country());
            country.setAreaSqkm(faker.number().numberBetween(1000, 1000000));
            country.setPopulation(faker.number().numberBetween(100000, 100000000));
            countryRepository.saveCountry(country);
        }

        // Получение списка всех стран
        List<Country> countries = countryRepository.getAllCountries();

        // Генерация олимпийских игр
        for (int i = 0; i < count; i++) {
            Olympic olympic = new Olympic();
            olympic.setOlympicId(faker.regexify("[A-Z0-9]{7}"));
            olympic.setCountryId(countries.get(faker.number().numberBetween(0, countries.size())).getCountryId());
            olympic.setCity(faker.address().city());
            olympic.setYear(faker.number().numberBetween(1896, 2024));
            olympic.setStartDate(faker.date().past(10000, java.util.concurrent.TimeUnit.DAYS));
            olympic.setEndDate(faker.date().future(100, java.util.concurrent.TimeUnit.DAYS));
            olympicRepository.saveOlympic(olympic);
        }

        // Генерация игроков
        for (int i = 0; i < count; i++) {
            Player player = new Player();
            player.setPlayerId(faker.regexify("[A-Z0-9]{10}"));
            player.setName(faker.name().fullName());
            player.setCountryId(countries.get(faker.number().numberBetween(0, countries.size())).getCountryId());
            player.setBirthDate(faker.date().birthday());
            playerRepository.savePlayer(player);
        }

        // Генерация событий
        List<Olympic> olympics = olympicRepository.getAllOlympics();
        for (int i = 0; i < count; i++) {
            Event event = new Event();
            event.setEventId(faker.regexify("[A-Z0-9]{7}"));
            event.setName(faker.book().title());
            event.setEventType(faker.company().buzzword());
            event.setOlympicId(olympics.get(faker.number().numberBetween(0, olympics.size())).getOlympicId());
            event.setIsTeamEvent(faker.number().numberBetween(0, 1));
            event.setNumPlayersInTeam(faker.number().numberBetween(1, 10));
            event.setResultNotedIn(faker.lorem().sentence());
            eventRepository.saveEvent(event);
        }

        // Генерация результатов
        List<Event> events = eventRepository.getAllEvents();
        List<Player> players = playerRepository.getAllPlayers();
        for (int i = 0; i < count; i++) {
            Result result = new Result();
            result.setEventId(events.get(faker.number().numberBetween(0, events.size())).getEventId());
            result.setPlayerId(players.get(faker.number().numberBetween(0, players.size())).getPlayerId());
            result.setMedal(faker.options().option("GOLD", "SILVER", "BRONZE", "NONE"));
            result.setResult(faker.number().randomDouble(2, 0, 100));
            resultRepository.saveResult(result);
        }
    }

    public static void main(String[] args) {
        seedData(10);
    }
}
