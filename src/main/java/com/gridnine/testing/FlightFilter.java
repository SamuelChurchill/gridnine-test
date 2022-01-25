package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FlightFilter {
    static List<Flight> excludeFlightsByDateTime(List<Flight> flights,
                                                 FlightStatus status, TimePeriod period, LocalDateTime time) {
        List<Flight> filteredFlights = new ArrayList<>();

        if (status.equals(FlightStatus.DEPARTURE)) {
            if (period.equals(TimePeriod.BEFORE)) {
                flights.forEach(flight -> {
                    if (flight.getSegments()
                            .stream().anyMatch(segment -> segment.getDepartureDate().isAfter(time))) {
                        filteredFlights.add(flight);
                    }
                });
            } else if (period.equals(TimePeriod.AFTER))
                flights.forEach(flight -> {
                    if (flight.getSegments()
                            .stream().anyMatch(segment -> segment.getDepartureDate().isBefore(time))) {
                        filteredFlights.add(flight);
                    }
                });
        } else if (status.equals(FlightStatus.ARRIVAL)) {
            if (period.equals(TimePeriod.BEFORE)) {
                flights.forEach(flight -> {
                    if (flight.getSegments()
                            .stream().anyMatch(segment -> segment.getArrivalDate().isAfter(time))) {
                        filteredFlights.add(flight);
                    }
                });
            } else if (period.equals(TimePeriod.AFTER)) {
                flights.forEach(flight -> {
                    if (flight.getSegments()
                            .stream().anyMatch(segment -> segment.getArrivalDate().isBefore(time))) {
                        filteredFlights.add(flight);
                    }
                });
            }
        }
        return filteredFlights;
    }

    static List<Flight> excludeFlightsArrivalBeforeDeparture(List<Flight> flights) {
        List<Flight> filteredFlights = new ArrayList<>();

        flights.forEach(flight -> {
            if (flight.getSegments()
                    .stream().anyMatch(segment -> segment.getDepartureDate().isBefore(segment.getArrivalDate()))) {
                filteredFlights.add(flight);
            }
        });

        return filteredFlights;
    }

    static List<Flight> excludeFlightsPositionLastsMoreThanMaxMinutes(List<Flight> flights,
                                                                      long maxIntervalMinutes, Position position) {
        List<Flight> filteredFlights = new ArrayList<>();

        for (Flight flight : flights) {
            long difference = 0L;
            if (position.equals(Position.ON_THE_GROUND) && flight.getSegments().size() >= 2) {
                List<Segment> segments = flight.getSegments();
                for (int i = 1; i < segments.size(); i++) {
                    difference += ChronoUnit.MINUTES.between(segments.get(i - 1).getArrivalDate(),
                            segments.get(i).getDepartureDate());
                }
            } else if (position.equals(Position.IN_THE_AIR)) {
                List<Segment> segments = flight.getSegments();
                for (Segment segment : segments) {
                    difference += ChronoUnit.MINUTES.between(segment.getDepartureDate(),
                            segment.getArrivalDate());
                }
            }
            if (difference < maxIntervalMinutes) {
                filteredFlights.add(flight);
            }
        }

        return filteredFlights;
    }
}
