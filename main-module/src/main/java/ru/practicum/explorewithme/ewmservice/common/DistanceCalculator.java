package ru.practicum.explorewithme.ewmservice.common;

import org.springframework.stereotype.Component;

import static java.lang.Math.*;

@Component
public class DistanceCalculator {

    /**
     * Calculates the distance between two points on the Earth specified by their latitude and longitude.
     *
     * @param lat1 Latitude of the first point in degrees.
     * @param lon1 Longitude of the first point in degrees.
     * @param lat2 Latitude of the second point in degrees.
     * @param lon2 Longitude of the second point in degrees.
     * @return Distance between the two points in kilometers.
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dist = 0.0;

        // Check if the two points are the same
        if (lat1 == lat2 && lon1 == lon2) {
            return dist; // Distance is 0 if both points are the same
        } else {
            // Convert degrees to radians
            double radLat1 = toRadians(lat1);
            double radLat2 = toRadians(lat2);
            double theta = lon1 - lon2;
            double radTheta = toRadians(theta);

            // Calculate the distance using the spherical law of cosines
            dist = sin(radLat1) * sin(radLat2) + cos(radLat1) * cos(radLat2) * cos(radTheta);

            // Ensure the distance does not exceed 1 (to avoid NaN from acos)
            if (dist > 1) {
                dist = 1;
            }

            // Calculate the arc cosine of the distance
            dist = acos(dist);
            // Convert radians to degrees
            dist = toDegrees(dist);
            // Convert degrees to kilometers (1 degree = 60 nautical miles, 1 nautical mile = 1.8524 km)
            dist = dist * 60 * 1.8524;

            return dist; // Return the calculated distance in kilometers
        }
    }
}
