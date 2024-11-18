package db;

import db.services.CountryService;
import db.services.EventService;
import db.services.PlayerService;

public class Main {
    public static void main(String[] args) {
        PlayerService playerService = new PlayerService();
        EventService eventService = new EventService();
        CountryService countryService = new CountryService();

        System.out.println("Задание 1: Игроки с золотыми медалями в 2004 году");
        playerService.getPlayersWithGoldMedalsIn2004().forEach(record -> {
            System.out.printf("Год рождения: %s, Количество игроков: %d, Количество золотых медалей: %d%n",
                    record[0].toString(), ((Long) record[1]).intValue(), ((Long) record[2]).intValue());
        });

        System.out.println("\nЗадание 2: Индивидуальные соревнования с ничьей и золотыми медалями");
        eventService.getIndividualEventsWithTies().forEach(record -> {
            System.out.printf("Событие: %s, Количество золотых медалей: %d%n",
                    record[0].toString(), ((Long) record[1]).intValue());
        });

        System.out.println("\nЗадание 3: Игроки, выигравшие хотя бы одну медаль на одной Олимпиаде");
        playerService.getPlayersWithMedals().forEach(record -> {
            System.out.printf("Имя игрока: %s, Олимпиада: %s%n", record[0].toString(), record[1].toString());
        });

        System.out.println("\nЗадание 4: Страна с наибольшим процентом игроков с именами на гласную");
        String countryWithHighestPercentage = playerService.getCountryWithHighestPercentageOfPlayersWithVowelStart();
        System.out.println("Страна: " + countryWithHighestPercentage);

        System.out.println("\nЗадание 5: Страны с минимальным соотношением групповых медалей к численности населения в 2000 году");
        countryService.getCountriesWithLowestTeamMedalRatio(5).forEach(record -> {
            System.out.printf("Страна: %s, Соотношение: %.6f%n",
                    record[0].toString(), (Double) record[1]);
        });
    }
}
