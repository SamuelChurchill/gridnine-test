package com.gridnine.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightFilterTest {

    static LocalDateTime now;
    static LocalDateTime threeDaysFromNow;
    static ArrayList<Flight> flights = new ArrayList<>();

    private static Flight createFlight(final LocalDateTime... dates) {
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException(
                    "you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }

    @BeforeAll
    static void setUp() {
        now = LocalDateTime.now();
        threeDaysFromNow = now.plusDays(3);

        //A normal flight with two hour duration
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)));
        //A normal multi segment flight
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)));
        //A flight departing in the past
        flights.add(createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow));
        //A flight that departs before it arrives
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)));
        //A flight with more than two hours ground time
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)));
        //Another flight with more than two hours ground time
        flights.add(createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7)));
    }

    @Test
    @DisplayName("Exclude flight departing in the past")
    void excludeFlightsByDateTime() {
        List<Flight> filteredFlights = FlightFilter.excludeFlightsByDateTime(flights, FlightStatus.DEPARTURE,
                TimePeriod.BEFORE, now);

        List<Flight> correctFlights = new ArrayList<>();
        correctFlights.add(flights.get(0));
        correctFlights.add(flights.get(1));
        correctFlights.add(flights.get(3));
        correctFlights.add(flights.get(4));
        correctFlights.add(flights.get(5));

        Assertions.assertEquals(correctFlights, filteredFlights);
    }

    @Test
    @DisplayName("Exclude flight that departs before it arrives")
    void excludeFlightsArrivalBeforeDeparture() {
        List<Flight> filteredFlights = FlightFilter.excludeFlightsArrivalBeforeDeparture(flights);

        List<Flight> correctFlights = new ArrayList<>();
        correctFlights.add(flights.get(0));
        correctFlights.add(flights.get(1));
        correctFlights.add(flights.get(2));
        correctFlights.add(flights.get(4));
        correctFlights.add(flights.get(5));

        Assertions.assertEquals(correctFlights, filteredFlights);
    }

    @Test
    @DisplayName("Exclude flight with more than two hours ground time")
    void excludeFlightsPositionLastsMoreThanMaxMinutes() {
        List<Flight> filteredFlights = FlightFilter.excludeFlightsPositionLastsMoreThanMaxMinutes(flights,
                120, Position.ON_THE_GROUND);

        List<Flight> correctFlights = new ArrayList<>();
        correctFlights.add(flights.get(0));
        correctFlights.add(flights.get(1));
        correctFlights.add(flights.get(2));
        correctFlights.add(flights.get(3));

        Assertions.assertEquals(correctFlights, filteredFlights);
    }

    @Test
    @DisplayName("Exclude flights departing in the past, flights that depart before arrival " +
            "and flights with more than two hours of ground time.")
    void combineFiltersApplied() {
        List<Flight> filteredFlights = FlightFilter.excludeFlightsByDateTime(flights, FlightStatus.DEPARTURE,
                TimePeriod.BEFORE, now);
        filteredFlights = FlightFilter.excludeFlightsArrivalBeforeDeparture(filteredFlights);
        filteredFlights = FlightFilter.excludeFlightsPositionLastsMoreThanMaxMinutes(filteredFlights,
                120, Position.ON_THE_GROUND);

        List<Flight> correctFlights = new ArrayList<>();
        correctFlights.add(flights.get(0));
        correctFlights.add(flights.get(1));

        Assertions.assertEquals(correctFlights, filteredFlights);
    }
}
