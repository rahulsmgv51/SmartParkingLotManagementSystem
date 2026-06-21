package com.rahulsmgv.parkinglot.config;

import com.rahulsmgv.parkinglot.entity.ParkingSpot;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import com.rahulsmgv.parkinglot.repository.ParkingSpotRepository;
import com.rahulsmgv.parkinglot.util.RedisKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheInitializer {

    private final ParkingSpotRepository spotRepo;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    ApplicationRunner loadRedisCache() {

        return args -> {

            log.info("Initializing Redis cache with current parking inventory");

            /*
             * Total spots
             */
            redisTemplate.opsForValue().set(
                    RedisKeys.TOTAL_SPOTS,
                    spotRepo.count());

            /*
             * Occupied spots
             */
            redisTemplate.opsForValue().set(
                    RedisKeys.OCCUPIED_SPOTS,
                    spotRepo.countByOccupiedTrue());

            /*
             * Available CAR spots
             */
            redisTemplate.opsForValue().set(
                    RedisKeys.AVAILABLE_CAR_SPOTS,
                    spotRepo.countByVehicleTypeAndOccupiedFalse(
                            VehicleType.CAR));

            /*
             * Available BIKE spots
             */
            redisTemplate.opsForValue().set(
                    RedisKeys.AVAILABLE_BIKE_SPOTS,
                    spotRepo.countByVehicleTypeAndOccupiedFalse(
                            VehicleType.BIKE));

            /*
             * Available TRUCK spots
             */
            redisTemplate.opsForValue().set(
                    RedisKeys.AVAILABLE_TRUCK_SPOTS,
                    spotRepo.countByVehicleTypeAndOccupiedFalse(
                            VehicleType.TRUCK));
        };
    }
}