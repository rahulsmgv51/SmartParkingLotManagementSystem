package com.rahulsmgv.parkinglot.util;

/*
 * Redis cache keys
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    public static final String TOTAL_SPOTS = "TOTAL_SPOTS";

    public static final String OCCUPIED_SPOTS = "OCCUPIED_SPOTS";

    public static final String ACTIVE_VEHICLES = "ACTIVE_VEHICLES";

    public static final String AVAILABLE_CAR_SPOTS = "AVAILABLE_CAR_SPOTS";

    public static final String AVAILABLE_BIKE_SPOTS = "AVAILABLE_BIKE_SPOTS";

    public static final String AVAILABLE_TRUCK_SPOTS = "AVAILABLE_TRUCK_SPOTS";
}