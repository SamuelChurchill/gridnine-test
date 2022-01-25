package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Список перелетов:");
        List<Flight> flights = FlightBuilder.createFlights();
        flights.forEach(System.out::println);

        System.out.println("\nСписок перелетов исключая те, " +
                "в которых имеются вылеты до текущего момента времени:");
        FlightFilter.excludeFlightsByDateTime(flights, FlightStatus.DEPARTURE,
                TimePeriod.BEFORE, LocalDateTime.now()).forEach(System.out::println);

        System.out.println("\nСписок перелетов исключая те, " +
                "в которых имеются сегменты с датой прилёта раньше даты вылета:");
        FlightFilter.excludeFlightsArrivalBeforeDeparture(flights).forEach(System.out::println);

        System.out.println("\nСписок перелетов исключая те, " +
                "в которых общее время, проведённое на земле превышает два часа:");
        FlightFilter.excludeFlightsPositionLastsMoreThanMaxMinutes(flights, 120,
                Position.ON_THE_GROUND).forEach(System.out::println);
    }
}
