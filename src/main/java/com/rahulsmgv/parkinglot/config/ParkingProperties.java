package com.rahulsmgv.parkinglot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "parking")
public class ParkingProperties {

    private Integer floors;

    private SpotConfig spots = new SpotConfig();

    @Getter
    @Setter
    public static class SpotConfig {

        private Integer car;
        private Integer bike;
        private Integer truck;
    }

    @PostConstruct
    public void verify() {
        log.info("ParkingProperties = {}", spots);

    }

    @Override
    public String toString() {
        return "floors=" + floors +
                ", car=" + spots.car +
                ", bike=" + spots.bike +
                ", truck=" + spots.truck;
    }
}